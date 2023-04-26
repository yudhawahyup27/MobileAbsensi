package com.nairobi.absensi.types

// Office data
class Office {
    var address: Address
    var startTime: Time
    var endTime: Time

    constructor(_address: Address = Address(), _startTime: Time = Time(), _endTime: Time = Time()) {
        address = _address
        startTime = _startTime
        endTime = _endTime
    }

    constructor(map: HashMap<String, Any>) {
        val addrMap = map.getOrDefault("address", Address().map())
        address = Address(addrMap as HashMap<String, Any>)
        val defaultTime = Time()
        startTime = Time(map.getOrDefault("startTime", defaultTime.unix()) as Long)
        endTime  = Time(map.getOrDefault("endTime", defaultTime.unix()) as Long)
    }

    // Get map representation of Office
    fun map(): HashMap<String, Any> {
        return hashMapOf(
            "address" to address.map(),
            "startTime" to startTime.unix(),
            "endTime" to endTime.unix()
        )
    }
}