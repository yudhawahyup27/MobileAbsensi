package com.nairobi.absensi.neuralnetwork

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions

class FaceDetector {
    private val detector: FirebaseVisionFaceDetector

    init {
        val opts = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.NO_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
            .build()
        detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(opts);
    }

    fun detect(image: Bitmap, callback: (Boolean, Bitmap?) -> Unit) {
        detector.detectInImage(FirebaseVisionImage.fromBitmap(image))
            .addOnSuccessListener { faces ->
                if (faces.isEmpty()) {
                    callback(false, null)
                    return@addOnSuccessListener
                }
                val face = faces[0]
                val boundingBox = face.boundingBox
                val bitmap = Bitmap.createBitmap(
                    image,
                    boundingBox.left,
                    boundingBox.top,
                    boundingBox.width(),
                    boundingBox.height()
                )
                val inputBmp = Bitmap.createScaledBitmap(bitmap, 112, 112, false)

                callback(true, inputBmp)
            }
            .addOnFailureListener {
                callback(false, null)
            }
    }

    fun detectFromURI(context: Context, uri: String, callback: (Boolean, Bitmap?) -> Unit) {
        val bitmap = MediaStore.Images.Media.getBitmap(
            context.contentResolver,
            Uri.parse(uri),
        )
        detect(bitmap, callback)
    }
}