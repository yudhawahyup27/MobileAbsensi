package com.nairobi.absensi.utils

// Validate email address
fun validateEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

// Validate string length
fun validateLength(string: String, lengthMin: Int, lengthMax: Int = 0): Boolean {
    return if (lengthMax == 0) {
        string.length >= lengthMin
    } else {
        string.length in lengthMin..lengthMax
    }
}

fun crashMe() {
    throw RuntimeException("Exception")
}