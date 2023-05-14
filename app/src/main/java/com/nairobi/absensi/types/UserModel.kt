package com.nairobi.absensi.types

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// User model
class UserModel {
    private val col: CollectionReference

    init {
        val db = Firebase.firestore
        col = db.collection("users")
    }

    private fun docToUser(doc: DocumentSnapshot): User {
        val map = doc.data?.toMutableMap()
        map?.put("id", doc.id)
        return User(map as HashMap<String, Any>)
    }

    // Get user by id
    fun getUserById(id: String, callback: (User?) -> Unit) {
        col.document(id).get().addOnSuccessListener {
            if (it.exists()) {
                callback(docToUser(it))
            } else {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
        }
    }

    // Get all user, and filter it if present
    fun getUsers(filter: (User) -> Boolean, callback: (ArrayList<User>) -> Unit) {
        col.get().addOnSuccessListener {
            val users: ArrayList<User> = arrayListOf()
            it.forEach {doc ->
                val user = docToUser(doc)
                if (filter(user)) users.add(user)
            }
            callback(users)
        }.addOnFailureListener {
            callback(arrayListOf())
        }
    }

    // Get user that match given key value
    fun getUser(filter: HashMap<String, Any>, callback: (User?) -> Unit) {
        var query: Query? = null
        filter.entries.forEach {
            if (query != null) {
                query!!.whereEqualTo(it.key, it.value)
            } else {
                query = col.whereEqualTo(it.key, it.value)
            }
        }
        if (query != null) {
            query!!.get().addOnSuccessListener {
                if (it.documents.isNotEmpty()) {
                    callback(docToUser(it.documents[0]))
                } else {
                    callback(null)
                }
            }.addOnFailureListener {
                Log.d("getUser", it.toString())
                callback(null)
            }
        } else {
            callback(null)
        }
    }

    // Create new user
    fun setUser(user: User, callback: (Boolean, String) -> Unit) {
        val map = user.map()
        map.remove("id")
        col.add(map).addOnSuccessListener {
            callback(true, it.id)
        }.addOnFailureListener {
            callback(false, "")
        }
    }

    // Update user
    fun updateUser(user: User, callback: (Boolean) -> Unit) {
        val map = user.map()
        map.remove("id")
        col.document(user.id).update(map).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    // Delete user
    fun deleteUser(user: User, callback: (Boolean) -> Unit) {
        col.document(user.id).delete().addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }
}