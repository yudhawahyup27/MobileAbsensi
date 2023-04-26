package com.nairobi.absensi.types

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LeaveRequestModel {
    private lateinit var col: CollectionReference

    init {
        val db = Firebase.firestore
        col = db.collection("leave")
    }

    private fun docToLeaveRequest(doc: DocumentSnapshot): LeaveRequest {
        val map = doc.data?.toMutableMap()
        map?.put("id", doc.id)
        return LeaveRequest(map as HashMap<String, Any>)
    }

    // Get leave request by id
    fun getLeaveRequest(id: String, callback: (LeaveRequest?) -> Unit) {
        col.document(id).get().addOnSuccessListener { doc ->
            callback(docToLeaveRequest(doc))
        }.addOnFailureListener {
            callback(null)
        }
    }

    // Get leave request by user id
    fun getLeaveRequestByUser(userId: String, callback: (ArrayList<LeaveRequest>) -> Unit) {
        col.whereEqualTo("userId", userId).get().addOnSuccessListener { documents ->
            val requests = arrayListOf<LeaveRequest>()
            documents.forEach { doc ->
                requests.add(docToLeaveRequest(doc))
            }
            callback(requests)
        }.addOnFailureListener {
            callback(arrayListOf())
        }
    }

    // Get all leave requests
    fun getLeaveRequests(callback: (ArrayList<LeaveRequest>) -> Unit) {
        col.get().addOnSuccessListener { documents ->
            val requests = arrayListOf<LeaveRequest>()
            documents.forEach { doc ->
                requests.add(docToLeaveRequest(doc))
            }
            callback(requests)
        }.addOnFailureListener {
            callback(arrayListOf())
        }
    }

    // Set leave request
    fun setLeaveRequest(request: LeaveRequest, callback: (Boolean) -> Unit) {
        col.add(request.toMap()).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }
}