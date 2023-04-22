package com.nairobi.absensi.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.util.Date

// Absence status
enum class AbsenceStatus {
    WORK,
    LEAVE,
    HOLIDAY,
    UNKNOWN,
}

// Leave status
enum class LeaveStatus {
    APPROVED,
    PENDING,
    REJECTED,
}

// Absence date
data class AbsenceDate(
    var year: Int,
    var month: Int,
    var day: Int,
    var hour: Int = 0,
    var minute: Int = 0,
) {
    // Convert AbsenceDate to HashMap
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            "year" to year,
            "month" to month,
            "day" to day,
            "hour" to hour,
            "minute" to minute,
        )
    }

    // Convert AbsenceDate to JSONObject
    fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("year", year)
        json.put("month", month)
        json.put("day", day)
        json.put("hour", hour)
        json.put("minute", minute)
        return json
    }

    // Convert AbsenceDate to JSON string
    fun toJSONString(): String {
        return toJSON().toString()
    }

    // Check if AbsenceDate is today
    fun isToday(): Boolean {
        val date = Date()
        return date.year + 1900 == year && date.month + 1 == month && date.date == day
    }

    companion object {
        // Create AbsenceDate from HashMap
        fun fromMap(map: HashMap<String, Any>): AbsenceDate {
            return AbsenceDate(
                map["year"] as Int,
                map["month"] as Int,
                map["day"] as Int,
                map["hour"] as Int,
                map["minute"] as Int,
            )
        }

        // Create AbsenceDate from JSONObject
        fun fromJSON(json: JSONObject): AbsenceDate {
            return AbsenceDate(
                json.getInt("year"),
                json.getInt("month"),
                json.getInt("day"),
                json.getInt("hour"),
                json.getInt("minute"),
            )
        }

        // Create AbsenceDate from Date
        fun fromDate(date: Date): AbsenceDate {
            return AbsenceDate(
                date.year + 1900,
                date.month + 1,
                date.date,
                date.hours,
                date.minutes,
            )
        }
    }
}

// Absence data
data class AbsenceData(
    var id: String,
    var status: AbsenceStatus,
    var date: AbsenceDate,
    var userId: String,
    var leaveStatus: LeaveStatus,
) {
    // Convert AbsenceData to HashMap
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "status" to status,
            "date" to date,
            "userId" to userId,
            "leaveStatus" to leaveStatus,
        )
    }

    // Convert AbsenceData to JSONObject
    fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("status", status)
        json.put("date", date.toJSON())
        json.put("userId", userId)
        json.put("leaveStatus", leaveStatus)
        return json
    }

    // Convert AbsenceData to JSON string
    fun toJSONString(): String {
        return toJSON().toString()
    }

    companion object {
        // Create AbsenceData from HashMap
        fun fromMap(map: HashMap<String, Any>): AbsenceData {
            return AbsenceData(
                map["id"] as String,
                AbsenceStatus.valueOf(map["status"] as String),
                AbsenceDate.fromMap(map["date"] as HashMap<String, Any>),
                map["userId"] as String,
                LeaveStatus.valueOf(map["leaveStatus"] as String),
            )
        }

        // Create AbsenceData from JSONObject
        fun fromJSON(json: JSONObject): AbsenceData {
            return AbsenceData(
                json.getString("id"),
                AbsenceStatus.valueOf(json.getString("status")),
                AbsenceDate.fromJSON(json.getJSONObject("date")),
                json.getString("userId"),
                LeaveStatus.valueOf(json.getString("leaveStatus")),
            )
        }

        // Create AbsenceData from JSON string
        fun fromJSONString(jsonString: String): AbsenceData {
            return fromJSON(JSONObject(jsonString))
        }

        // Create holiday AbsenceData
        fun createHolidayAbsenceData(userId: String): AbsenceData {
            return AbsenceData(
                "",
                AbsenceStatus.HOLIDAY,
                AbsenceDate.fromDate(Date()),
                userId,
                LeaveStatus.APPROVED,
            )
        }
    }
}

// Absence model
class AbsenceModel {
    private val db = Firebase.firestore
    private val collection = db.collection("absence")

    // Convert document to AbsenceData
    private fun docToAbsenceData(doc: DocumentSnapshot): AbsenceData {
        val map = doc.data?.toMutableMap()
        map?.put("id", doc.id)
        return AbsenceData.fromMap(map as HashMap<String, Any>)
    }

    // Get absence data by id
    fun getAbsenceDataById(id: String, callback: (AbsenceData?) -> Unit) {
        collection.document(id).get().addOnSuccessListener {
            callback(docToAbsenceData(it))
        }.addOnFailureListener {
            callback(null)
        }
    }

    // Get absence data by user id
    fun getAbsenceDataByUserId(userId: String, callback: (ArrayList<AbsenceData>) -> Unit) {
        collection.whereEqualTo("userId", userId).get().addOnSuccessListener {
            val list = ArrayList<AbsenceData>()
            for (doc in it.documents) {
                list.add(docToAbsenceData(doc))
            }
            callback(list)
        }.addOnFailureListener {
            callback(arrayListOf())
        }
    }

    // Create absence data
    fun createAbsenceData(absenceData: AbsenceData, callback: (Boolean) -> Unit) {
        collection.document().set(absenceData.toMap()).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    // Update absence data
    fun updateAbsenceData(absenceData: AbsenceData, callback: (Boolean) -> Unit) {
        collection.document(absenceData.id).set(absenceData.toMap()).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }
}