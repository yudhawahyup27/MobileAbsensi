package com.nairobi.absensi.types

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Overtime model
class OvertimeModel {
    private lateinit var col: CollectionReference

    init {
        val db = Firebase.firestore
        col = db.collection("overtime")
    }

    private fun docToOvertime(doc: DocumentSnapshot): Overtime {
        val map = doc.data?.toMutableMap()
        map?.put("id", doc.id)
        return Overtime(map as HashMap<String, Any>)
    }

    // Get overtime by id
    fun getOvertime(id: String, callback: (Overtime?) -> Unit) {
        col.document(id).get().addOnSuccessListener {
            callback(docToOvertime(it))
        }.addOnFailureListener {
            callback(null)
        }
    }

    // Get all overtime
    fun getAllOvertime(callback: (ArrayList<Overtime>) -> Unit) {
        col.get().addOnSuccessListener {
            val list = ArrayList<Overtime>()
            for (doc in it) {
                list.add(docToOvertime(doc))
            }
            callback(list)
        }.addOnFailureListener {
            callback(arrayListOf())
        }
    }

    // Get overtime by user id
    fun getOvertimeByUserId(userId: String, callback: (ArrayList<Overtime>) -> Unit) {
        col.whereEqualTo("userId", userId).get().addOnSuccessListener {
            val list = ArrayList<Overtime>()
            for (doc in it) {
                list.add(docToOvertime(doc))
            }
            callback(list)
        }.addOnFailureListener {
            callback(arrayListOf())
        }
    }

    // Update overtime
    fun updateOvertime(overtime: Overtime, callback: (Boolean) -> Unit) {
        val map = overtime.toMap()
        map.remove("id")
        col.document(overtime.id).update(map).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }
}