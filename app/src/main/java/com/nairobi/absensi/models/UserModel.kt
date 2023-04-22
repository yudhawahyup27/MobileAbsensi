package com.nairobi.absensi.models

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nairobi.absensi.utils.getAddressFromLocation
import com.nairobi.absensi.utils.getLocationFromAddress
import com.nairobi.absensi.utils.getLocationFromLatLong
import com.nairobi.absensi.utils.validateEmail
import com.nairobi.absensi.utils.validateLength
import org.json.JSONObject
import java.util.Date

// User role
enum class UserRole {
    ADMIN,
    USER
}

// User
data class User(
    var id: String,
    var email: String,
    var password: String,
    var phone: String,
    var nip: String,
    var name: String,
    var address: Address,
    var role: UserRole,
    var dob: Date
) {
    // Convert User to HashMap
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "email" to email,
            "password" to password,
            "phone" to phone,
            "nip" to nip,
            "name" to name,
            "address" to address.toMap(),
            "role" to role.name,
            "dob" to dob
        )
    }

    // Convert Uset to JSONObject
    fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("email", email)
        json.put("password", password)
        json.put("phone", phone)
        json.put("nip", nip)
        json.put("name", name)
        json.put("address", address.toJSON())
        json.put("role", role.name)
        json.put("dob", dob)
        return json
    }

    // Convert User to JSON string
    fun toJSONString(): String {
        return toJSON().toString()
    }

    // Check if user is admin
    fun isAdmin(): Boolean {
        return role == UserRole.ADMIN
    }

    // Check if user field is valid
    fun isValid(callback: (Boolean, String) -> Unit) {
        val emailValid = !email.isEmpty() && validateEmail(email)
        val passwordValid = !password.isEmpty() && validateLength(password, 6)
        val phoneValid = !phone.isEmpty() && validateLength(phone, 10, 13)
        val nipValid = !nip.isEmpty()
        val nameValid = !name.isEmpty()
        val addressValid = address.latitude != 0.0 && address.longitude != 0.0
        val dobValid = dob != Date(0)
        when {
            !emailValid -> callback(false, "Email tidak valid")
            !passwordValid -> callback(false, "Password minimal 6 karakter")
            !phoneValid -> callback(false, "Nomor telepon tidak valid")
            !nipValid -> callback(false, "NIP tidak valid")
            !nameValid -> callback(false, "Nama tidak valid")
            !addressValid -> callback(false, "Alamat tidak valid")
            !dobValid -> callback(false, "Tanggal lahir tidak valid")
            else -> callback(true, "")
        }

    }

    companion object {
        // Create User from HashMap
        fun fromMap(map: HashMap<String, Any>): User {
            return User(
                map["id"] as String,
                map["email"] as String,
                map["password"] as String,
                map["phone"] as String,
                map["nip"] as String,
                map["name"] as String,
                Address.fromMap(map["address"] as HashMap<String, Any>),
                UserRole.valueOf(map["role"] as String),
                (map["dob"] as Timestamp).toDate()
            )
        }

        // Create User from JSONObject
        fun fromJSON(json: JSONObject): User {
            return User(
                json.getString("id"),
                json.getString("email"),
                json.getString("password"),
                json.getString("phone"),
                json.getString("nip"),
                json.getString("name"),
                Address.fromJSON(json.getJSONObject("address")),
                UserRole.valueOf(json.getString("role")),
                Date(json.getString("dob"))
            )
        }

        // Create User from JSON string
        fun fromJSONString(json: String): User {
            return fromJSON(JSONObject(json))
        }

        // Create empty User
        fun empty(): User {
            return User(
                "",
                "",
                "",
                "",
                "",
                "",
                Address.empty(),
                UserRole.USER,
                Date()
            )
        }
    }
}

// User model
class UserModel {
    private val db = Firebase.firestore
    private val collection = db.collection("users")

    // Convert document to user
    private fun docToUser(doc: DocumentSnapshot): User {
        val map = doc.data?.toMutableMap()
        map?.put("id", doc.id)
        return User.fromMap(map as HashMap<String, Any>)
    }

    // Get user by id
    fun getUserById(id: String, callback: (User?) -> Unit) {
        collection.document(id).get().addOnSuccessListener {
            if (it.exists()) {
                callback(docToUser(it))
            } else {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
        }
    }

    // Get user by given key and value
    fun getUserByKey(key: String, value: Any, callback: (User?) -> Unit) {
        collection.whereEqualTo(key, value).get().addOnSuccessListener {
            if (it.documents.isNotEmpty()) {
                callback(docToUser(it.documents[0]))
            } else {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
        }
    }

    // Get all users with optional filter
    fun getUsers(filter: (User) -> Boolean = { true }, callback: (ArrayList<User>) -> Unit) {
        collection.get().addOnSuccessListener {
            val users = ArrayList<User>()
            for (document in it.documents) {
                val user = docToUser(document)
                if (filter(user)) {
                    users.add(user)
                }
            }
            callback(users)
        }.addOnFailureListener {
            callback(arrayListOf())
        }
    }

    // Create user
    fun createUser(user: User, callback: (Boolean, String) -> Unit) {
        val map = user.toMap()
        map.remove("id")
        collection.add(map).addOnSuccessListener {
            callback(true, it.id)
        }.addOnFailureListener {
            callback(false, "")
        }
    }

    // Update user
    fun updateUser(user: User, callback: (Boolean) -> Unit) {
        collection.document(user.id).update(user.toMap()).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    // Delete user
    fun deleteUser(user: User, callback: (Boolean) -> Unit) {
        collection.document(user.id).delete().addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }
}