package com.nairobi.absensi.neuralnet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Objects
import kotlin.math.sqrt

private const val IMAGE_MEAN = 128.0f
private const val IMAGE_STD = 128.0f

// Face recognition
class FaceRecognition(context: Context) {
    private val model: Interpreter
    private val inputSize = 112
    private val registered: HashMap<String, Recognition> = HashMap()
    private val intValues: IntArray = IntArray(inputSize * inputSize)
    private var imgData: ByteBuffer
    private var embeedings: Array<FloatArray> = Array(1) { FloatArray(192) }

    // Register face
    fun register(name: String, face: Recognition) {
        registered[name] = face
    }

    init {
        val options = Interpreter.Options().apply {
            if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                addDelegate(GpuDelegate(CompatibilityList().bestOptionsForThisDevice))
            } else {
                numThreads = 4
            }
            setUseXNNPACK(true)
            useNNAPI = true
        }
        model = Interpreter(FileUtil.loadMappedFile(context, "ramdhan.tflite"), options)
        imgData = ByteBuffer.allocateDirect(inputSize * inputSize * 3 * 4)
        imgData.order(ByteOrder.nativeOrder())
    }

    // Recognize face
    fun recognize(bitmapx: Bitmap): ArrayList<Recognition> {
        // make sure the image is match input size, if not resize it
        var  bitmap = bitmapx
        if (bitmap.width != inputSize || bitmap.height != inputSize) {
            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap,
                inputSize,
                inputSize,
                false
                )
            bitmap.recycle()
            bitmap = scaledBitmap
        }

        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        imgData.rewind()
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = intValues[i * inputSize + j]
                imgData.putFloat((((pixelValue shr 16) and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                imgData.putFloat((((pixelValue shr 8) and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                imgData.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }

        val inputArray = arrayOf(imgData)
        val outputMap = HashMap<Int, Any>()

        embeedings = Array(1) { FloatArray(192) }
        outputMap[0] = embeedings
        model.runForMultipleInputsOutputs(inputArray, outputMap)

        var distance = Float.MAX_VALUE
        var label = ""
        if (registered.isNotEmpty()) {
            val nearest = findNearest(embeedings[0])
            nearest?.let {
                label = it.first
                distance = it.second
            }
        }

        val recognitions = ArrayList<Recognition>(1)
        val rec = Recognition(
            label,
            distance,
            RectF()
        )
        rec.extra = embeedings
        recognitions.add(rec)
        return recognitions
    }

    private fun findNearest(embeeding: FloatArray): Pair<String, Float>? {
        var res: Pair<String, Float>? = null
        registered.entries.forEach {
            val name = it.key
            val knownEmb = it.value.extra!![0]
            var distance = 0.toFloat()
            for (i in embeeding.indices) {
                val diff = embeeding[i] - knownEmb[i]
                distance += diff * diff
            }
            distance = sqrt(distance)
            if (res == null || distance < res!!.second) {
                res = Pair(name, distance)
            }
        }

        return res
    }
}