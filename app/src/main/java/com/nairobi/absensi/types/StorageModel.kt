package com.nairobi.absensi.types

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

// Storage model
class StorageModel {
//    private val storage = Firebase.storage("gs://absen-1dc50.appspot.com")
    private val storage = Firebase.storage("gs://freelance-absensi.appspot.com/")
    private val storageRef = storage.reference

    // Upload file to storage
    private fun uploadFile(path: String, file: ByteArray, callback: (String) -> Unit) {
        val fileRef = storageRef.child(path)
        fileRef.putBytes(file).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener {
                callback(it.toString())
            }
        }.addOnFailureListener {
            callback("error")
        }
    }

    // Upload bitmap to storage
    fun uploadBitmap(path: String, bitmap: Bitmap, callback: (Boolean) -> Unit) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        uploadFile(path, data) {
            callback(it != "error")
        }
    }

    // get file from storage as byte array
    private fun getFileAsByteArray(path: String, callback: (ByteArray) -> Unit) {
        val fileRef = storageRef.child(path)
        fileRef.downloadUrl.addOnSuccessListener {
            fileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
                callback(it)
            }.addOnFailureListener {
                callback(byteArrayOf())
            }
        }.addOnFailureListener {
            callback(byteArrayOf())
        }
    }

    // get file from storage as bitmap
    fun getFileAsBitmap(path: String, callback: (Bitmap?) -> Unit) {
        getFileAsByteArray(path) {
            if (it.isEmpty()) {
                callback(null)
            } else {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                callback(bitmap)
            }
        }
    }

    // check file exist
    fun checkFile(path: String, callback: (Boolean) -> Unit) {
        if (path.isEmpty()) {
            callback(false)
            return
        }
        val fileRef = storageRef.child(path)
        fileRef.downloadUrl.addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    // upload file from uri
    fun uploadWithUri(context: Context, path: String, uri: String, callback: (Boolean) -> Unit) {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(uri))
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        uploadFile(path, data) {
            callback(it != "error")
        }
    }

    // delete file from storage
    fun deleteFile(path: String, callback: (Boolean) -> Unit) {
        val fileRef = storageRef.child(path)
        fileRef.delete().addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }
}