package com.nairobi.absensi.types

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Absence Model
class AbsenceModel {
    private val col: CollectionReference

    init {
        val db = Firebase.firestore
        col = db.collection("absence")
    }

    private fun docToAbsence(doc: DocumentSnapshot): Absence {
        val map = doc.data?.toMutableMap()
        map?.put("id", doc.id)
        return Absence(map as HashMap<String, Any>)
    }

    // Get absence by id
    fun getAbsenceById(id: String, callback: (Absence?) -> Unit) {
        col.document(id).get().addOnSuccessListener {
            callback(docToAbsence(it))
        }.addOnFailureListener {
            callback(null)
        }
    }

    // Get absence by userId
    fun getAbsenceByUserId(userId: String, callback: (ArrayList<Absence>) -> Unit) {
        col.whereEqualTo("userId", userId).get().addOnSuccessListener {
            val absences = arrayListOf<Absence>()
            it.documents.forEach { doc ->
                absences.add(docToAbsence(doc))
            }
            callback(absences)
        }.addOnFailureListener {
            callback(arrayListOf())
        }
    }

    // Get all absence
    fun getAbsences(callback: (ArrayList<Absence>) -> Unit) {
        col.get().addOnSuccessListener {
            val absences = arrayListOf<Absence>()
            it.documents.forEach { doc ->
                absences.add(docToAbsence(doc))
            }
            callback(absences)
        }.addOnFailureListener {
            callback(arrayListOf())
        }
    }

    // Add absence
    fun addAbsence(absence: Absence, callback: (Boolean) -> Unit) {
        val map = absence.map()
        map.remove("id")
        col.add(map).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun addAbsenceout(absence: Absence, callback: (Boolean) -> Unit) {
        val map = absence.map()
        map.remove("id")
        col.add(map).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }
    // Update multiple absence
    fun updateAbsences(absences: ArrayList<Absence>, callback: (Boolean) -> Unit) {
        val batch = Firebase.firestore.batch()
        absences.forEach { absence ->
            val doc = col.document(absence.id)
            batch.update(doc, absence.map())
        }
        batch.commit().addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }
}