package com.nairobi.absensi.types

import android.content.Context
import android.location.Geocoder
import android.location.Location

// Address representation
class Address {
    private var _location: Location

    constructor() {
        _location = Location("")
    }

    constructor(latitude: Double, longitude: Double) {
        _location = Location("")
        _location.latitude = latitude
        _location.longitude = longitude
    }

    constructor(location: Location) {
        _location = location
    }

    constructor(map: HashMap<String, Any>) {
        _location = Location("")
        _location.latitude = map.getOrDefault("latitude", 0.0) as Double
        _location.longitude = map.getOrDefault("longitude", 0.0) as Double
    }

    var latitude: Double
        get() = _location.latitude
        set(value) {
            _location.latitude = value
        }

    var longitude: Double
        get() = _location.longitude
        set(value) {
            _location.longitude
        }

    // String representation of address
    fun string(context: Context): String {
        val coder = Geocoder(context)
        val addresses = coder.getFromLocation(latitude, longitude, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                return addresses[0].getAddressLine(0)
            }
        }
        return ""
    }

    // HashMap representation of address
    fun map(): HashMap<String, Any> {
        return hashMapOf(
            "latitude" to latitude,
            "longitude" to longitude,
        )
    }

    // Check if address is near another address

    fun near(address: Address, meters: Int): Boolean {
       val loc1 = Location("")
        loc1.latitude = latitude
        loc1.longitude = longitude
        val loc2 = Location("")
        loc2.latitude = address.latitude
        loc2.longitude = address.longitude
        return loc1.distanceTo(loc2) <= meters
    }
}