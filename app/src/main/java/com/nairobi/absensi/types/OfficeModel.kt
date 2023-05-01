package com.nairobi.absensi.types

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// User model
class OfficeModel {
    private val col: CollectionReference

    init {
        val db = Firebase.firestore
        col = db.collection("office")
    }

    private fun docToOffice(doc: DocumentSnapshot): Office {
        val map = doc.data?.toMutableMap() ?: return Office()
        return Office(map as HashMap<String, Any>)
    }

    // Get office
    fun getOffice(callback: (Office) -> Unit) {
        col.document("data").get().addOnSuccessListener {
            callback(docToOffice(it))
        }.addOnFailureListener {
            callback(Office())
        }
    }

    // Update office
    fun updateOffice(office: Office, callback: (Boolean) -> Unit) {
        col.document("data").set(office.map()).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }
}