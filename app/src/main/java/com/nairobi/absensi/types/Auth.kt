package com.nairobi.absensi.types

import android.content.Context
import android.content.SharedPreferences

// Authentication provider
class Auth {
    companion object {
        private lateinit var ref: SharedPreferences
        private var _user: User? = null
        private const val key = "userId"
        private var listener: (Boolean) -> Unit = {}

        // Initialize
        fun init(context: Context, callback: () -> Unit) {
            ref = context.getSharedPreferences(context.packageName, 0)
            val userId = getId()
            userId?.let {id ->
                UserModel().getUserById(id) {
                    if (it == null) {
                        removeId()
                    }
                    _user = it
                    callback()
                }
            } ?: callback()
        }

        // check if user is logged in
        fun isLoggedIn(): Boolean {
            return _user != null
        }

        // Listen to changes
        fun listen(callback: (Boolean) -> Unit) {
            listener = callback
        }

        // Notify changes
        fun notifyListener() {
            listener(isLoggedIn())
        }

        // Get id
        private fun getId(): String? {
            return ref.getString(key, null)
        }

        // Set id
        private fun setId(id: String) {
            ref.edit().putString(key, id).apply()
        }

        // Remove id
        private fun removeId() {
            ref.edit().remove(key).apply()
        }

        // Login
        fun login(email: String, password: String, callback: (Boolean) -> Unit) {
            UserModel().getUser(
                hashMapOf(
                    "email" to email,
                )
            ) {
                if (it == null || it.password != password) {
                    callback(false)
                } else {
                    _user = it
                    setId(it.id)
                    notifyListener()
                    callback(true)
                }
            }
        }

        // Logout
        fun logout() {
            _user = null
            removeId()
            notifyListener()
        }

        var user: User?
            get() = _user
            set(value) {
                _user = value
            }
    }
}