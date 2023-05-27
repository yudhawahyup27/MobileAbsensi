package com.nairobi.absensi.types

// Overtime Status
enum class OvertimeStatus {
    APPROVED,
    PENDING,
    REJECTED,
}

// Overtime
class Overtime {
    var id: String
    var userId: String
    var start: Time
    var end: Time
    var date: Date
    var detaillembur: String
    var status: OvertimeStatus

    constructor(
        _id: String = "",
        _userId: String = "",
        _detaillembur: String ="",
        _start: Time = Time(),
        _end: Time = Time(),
        _date: Date = Date(),
        _status: OvertimeStatus = OvertimeStatus.PENDING
    ) {
        id = _id
        userId = _userId
        start = _start
        detaillembur = _detaillembur
        end = _end
        date = _date
        status = _status
    }

    constructor(map: HashMap<String, Any>) {
        id = map.getOrDefault("id", "").toString()
        userId = map.getOrDefault("userId", "").toString()
        detaillembur = map.getOrDefault("detaillembur", "").toString()
        start = Time(map.getOrDefault("start", Time().unix()).toString().toLong())
        end = Time(map.getOrDefault("end", Time().unix()).toString().toLong())
        date = Date(map.getOrDefault("date", Date().unix()).toString().toLong())
        status = OvertimeStatus.valueOf(map.getOrDefault("status", "PENDING").toString())
    }

    //  Convert to HashMap
    fun toMap(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["id"] = id
        map["userId"] = userId
        map["detaillembur"] = detaillembur
        map["start"] = start.unix()
        map["end"] = end.unix()
        map["date"] = date.unix()
        map["status"] = status
        return map
    }
}