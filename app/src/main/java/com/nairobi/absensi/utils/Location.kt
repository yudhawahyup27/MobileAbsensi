package com.nairobi.absensi.utils

import android.content.Context
import android.location.Geocoder
import android.location.Location
import java.util.Locale

// Get location from latitude and longitude
fun getLocationFromLatLong(latitude: Double, longitude: Double): Location {
    val location = Location("")
    location.latitude = latitude
    location.longitude = longitude
    return location
}

// Get location from address string
fun getLocationFromAddress(context: Context, address: String): Location {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocationName(address, 1)
    if (addresses != null) {
        if (addresses.isNotEmpty()) {
            val address = addresses[0]
            val location = Location("")
            location.latitude = address.latitude
            location.longitude = address.longitude
            return location
        }
    }
    return Location("")
}

// Get address from location
fun getAddressFromLocation(context: Context, location: Location): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
    if (addresses != null) {
        if (addresses.isNotEmpty()) {
            val address = addresses[0]
            return address.getAddressLine(0)
        }
    }
    return ""
}

// Calculate distance between two locations in meters
fun calculateDistanceBetweenLocations(location1: Location, location2: Location): Float {
    return location1.distanceTo(location2)
}