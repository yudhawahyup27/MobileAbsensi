package com.nairobi.absensi.auth

import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import com.nairobi.absensi.models.User
import com.nairobi.absensi.models.UserModel

// Authentication provider
class Auth {
    companion object {
        private var ref: SharedPreferences? = null
        private var user: User? = null
        private var listener: (Boolean) -> Unit = {}

        // Listen to changes
        fun listen(callback: (Boolean) -> Unit) {
            listener = callback
        }

        // Notify listener
        private fun notifyListener() {
            listener(isLoggedIn())
        }

        // Initialize
        fun init(app: ComponentActivity, callback: () -> Unit) {
            ref = app.getSharedPreferences("com.nairobi.absensi", 0)
            val userID = ref?.getString("user_id", null)
            val model = UserModel()
            if (userID != null) {
                model.getUserByKey("id", userID) {
                    user = it
                    notifyListener()
                    callback()
                }
            } else {
                callback()
            }
        }

        // Login
        fun login(email: String, password: String, callback: (User?) -> Unit) {
            val model = UserModel()
            model.getUserByKey("email", email) {
                if (it != null && it.password == password) {
                    ref?.edit()?.putString("user_id", it.id)?.apply()
                    user = it
                    notifyListener()
                    callback(user)
                } else {
                    callback(null)
                }
            }
        }

        // Check if user is logged in
        fun isLoggedIn(): Boolean {
            return user != null
        }

        // Get user
        fun getUser(): User? {
            return user
        }

        // Update user
        fun updateUser(newUser: User) {
            user = newUser
        }

        //  Logout
        fun logout() {
            ref?.edit()?.remove("user_id")?.apply()
            user = null
            notifyListener()
        }
    }
}