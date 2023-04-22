package com.nairobi.absensi.models

import android.content.Context
import com.nairobi.absensi.utils.getAddressFromLocation
import com.nairobi.absensi.utils.getLocationFromAddress
import com.nairobi.absensi.utils.getLocationFromLatLong
import org.json.JSONObject

// Address
data class Address(
    var longitude: Double,
    var latitude: Double
) {
    // Convert UserAddress to HashMap
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            "longitude" to longitude,
            "latitude" to latitude
        )
    }

    // Convert UserAddress to JSONObject
    fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("longitude", longitude)
        json.put("latitude", latitude)
        return json
    }

    // Convert UserAddress to JSON string
    fun toJSONString(): String {
        return toJSON().toString()
    }

    // Convert UserAddress to Address string
    fun toAddressString(context: Context): String {
        val location = getLocationFromLatLong(latitude, longitude)
        return getAddressFromLocation(context, location)
    }

    companion object {
        // Create UserAddress from HashMap
        fun fromMap(map: HashMap<String, Any>): Address {
            return Address(
                map["longitude"] as Double,
                map["latitude"] as Double
            )
        }

        // Create UserAddress from JSONObject
        fun fromJSON(json: JSONObject): Address {
            return Address(
                json.getDouble("longitude"),
                json.getDouble("latitude")
            )
        }

        // Create UserAddress from Address string
        fun fromAddressString(context: Context, address: String): Address {
            val location = getLocationFromAddress(context, address)
            return Address(location.longitude, location.latitude)
        }

        // Create UserAddress from JSON string
        fun fromJSONString(json: String): Address {
            return fromJSON(JSONObject(json))
        }

        // Create empty UserAddress
        fun empty(): Address {
            return Address(0.0, 0.0)
        }
    }
}

// Time
data class TimeData(
    val hour: Int,
    val minute: Int
) {
    // Convert to string
    fun string(): String {
        return "$hour:$minute"
    }

    companion object {
        // Convert from string
        fun fromString(string: String): TimeData {
            val split = string.split(":")
            return TimeData(split[0].toInt(), split[1].toInt())
        }
        // Create empty TimeData
        fun empty(): TimeData {
            return TimeData(0, 0)
        }
    }
}