package com.nairobi.absensi.utils

import android.content.Context
import com.nairobi.absensi.R
import com.nairobi.absensi.types.User

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

// Validate user field
fun validateUser(context: Context, user: User): Pair<Boolean, String> {
    if (!validateEmail(user.email)) {
        return false to context.getString(R.string.invalid_email)
    }
    if (!validateLength(user.password, 6)) {
        return false to context.getString(R.string.length_error_password)
    }
    if (user.name.isEmpty()) {
        return false to context.getString(R.string.empty_name)
    }
    if (user.nip.isEmpty()) {
        return false to context.getString(R.string.empty_nip)
    }
    if (user.phone.isEmpty()) {
        return false to context.getString(R.string.empty_phone)
    }
    return true to ""
}