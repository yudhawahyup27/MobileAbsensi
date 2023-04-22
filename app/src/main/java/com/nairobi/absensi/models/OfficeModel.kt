package com.nairobi.absensi.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

// Office data
data class OfficeData(
    var address: Address,
    var startTime: TimeData,
    var endTime: TimeData
) {
    // Convert OfficeData to HashMap
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            "address" to address.toMap(),
            "startTime" to startTime.string(),
            "endTime" to endTime.string()
        )
    }

    // Convert OfficeData to JSON
    fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("address", address.toJSON())
        json.put("startTime", startTime.string())
        json.put("endTime", endTime.string())
        return json
    }

    // Convert OfficeData to JSON string
    fun toJSONString(): String {
        return toJSON().toString()
    }

    companion object {
        // Create OfficeData from HashMap
        fun fromMap(map: HashMap<String, Any>): OfficeData {
            return OfficeData(
                Address.fromMap(map["address"] as HashMap<String, Any>),
                TimeData.fromString(map["startTime"] as String),
                TimeData.fromString(map["endTime"] as String)
            )
        }
        // Create OfficeData from JSON
        fun fromJSON(json: JSONObject): OfficeData {
            return OfficeData(
                Address.fromJSON(json.getJSONObject("address")),
                TimeData.fromString(json.getString("startTime")),
                TimeData.fromString(json.getString("endTime"))
            )
        }
        // Create OfficeData from JSON string
        fun fromJSONString(jsonString: String): OfficeData {
            return fromJSON(JSONObject(jsonString))
        }
        // Create empty OfficeData
        fun empty(): OfficeData {
            return OfficeData(
                Address.empty(),
                TimeData.empty(),
                TimeData.empty()
            )
        }
    }
}

// Office model
class OfficeModel {
    private val db = Firebase.firestore
    private val collection = db.collection("office")

    // Convert document to OfficeData
    private fun docToOfficeData(doc: DocumentSnapshot): OfficeData {
        val map = doc.data?.toMutableMap()
        map?.put("id", doc.id)
        return OfficeData.fromMap(map as HashMap<String, Any>)
    }

    // Get office data
    fun getOfficeData(callback: (OfficeData) -> Unit) {
        collection.document("data").get().addOnSuccessListener {
            if (it.exists()) {
                callback(docToOfficeData(it))
            } else {
                callback(OfficeData.empty())
            }
        }.addOnFailureListener {
            callback(OfficeData.empty())
        }
    }

    // Set office data
    fun setOfficeData(officeData: OfficeData, callback: (Boolean) -> Unit) {
        collection.document("data").set(officeData.toMap()).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }
}