package com.nairobi.absensi.types

// Leave request status
enum class LeaveRequestStatus {
    PENDING,
    APPROVED,
    REJECTED
}

// Leave request
class LeaveRequest {
    var id: String
    var start: Date
    var end: Date
    var userId: String
    var status: LeaveRequestStatus
    var reason: String

    constructor(
        _id: String = "",
        _start: Date = Date(),
        _end: Date = Date(),
        _userId: String = "",
        _status: LeaveRequestStatus = LeaveRequestStatus.PENDING,
        _reason: String = ""
    ) {
        id = _id
        start = _start
        end = _end
        userId = _userId
        status = _status
        reason = _reason
    }

    constructor(map: HashMap<String, Any>) {
        id = map.getOrDefault("id", "").toString()
        start = Date(map.getOrDefault("start", 0).toString().toLong())
        end = Date(map.getOrDefault("end", 0).toString().toLong())
        userId = map.getOrDefault("userId", "").toString()
        status = LeaveRequestStatus.valueOf(map.getOrDefault("status", LeaveRequestStatus.PENDING).toString())
        reason = map.getOrDefault("reason", "").toString()
    }

    // Get map representation of leave request
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "start" to start.unix(),
            "end" to end.unix(),
            "userId" to userId,
            "status" to status,
            "reason" to reason
        )
    }
}