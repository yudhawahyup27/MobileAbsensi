package com.nairobi.absensi.types

// Absence type
enum class AbsenceType {
    ONWORK,
    WORK,
    OUT,
    LEAVE,
    HOLIDAY,
    UNKNOWN,
}

// Absence data
class Absence {
    var id: String
    var userId: String
    var type: AbsenceType
    var date: Date
    var endDate: Date? = null

    constructor(
        _id: String = "",
        _userId: String = "",
        _type: AbsenceType = AbsenceType.UNKNOWN,
        _date: Date = Date(),
        _endDate: Date? = null
    ) {
        id = _id
        userId = _userId
        type = _type
        date = _date
        endDate = _endDate
    }

    constructor(map: HashMap<String, Any>) {
        id = map.getOrDefault("id", "").toString()
        userId = map.getOrDefault("userId", "").toString()
        type = AbsenceType.valueOf(map.getOrDefault("type", "UNKNOWN").toString())
        date = if (map["date"] != null) {
            Date(map["date"] as Long)
        } else {
            Date()
        }
        endDate = if (map["endDate"] != null && map["endDate"] != "") {
            Date(map["endDate"] as Long)
        } else {
            null
        }
    }

    // Get map representation of Absence
    fun map(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "userId" to userId,
            "type" to type,
            "date" to date.unix(),
            "endDate" to if (endDate != null) endDate!!.unix() else "",
        )
    }
}