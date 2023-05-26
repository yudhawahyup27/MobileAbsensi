package com.nairobi.absensi.types

// User role
enum class UserRole {
    ADMIN,
    USER,
}

// User representation
class User {
    var id: String
    var name: String
    var email: String
    var password: String
    var phone: String
    var address: Address
    var role: UserRole
    var dob: Date

    constructor(
        _id: String = "",
        _name: String = "",
        _email: String = "",
        _password: String = "",
        _phone: String = "",
        _address: Address = Address(),
        _role: UserRole = UserRole.USER,
        _dob: Date = Date()
    ) {
        id = _id
        name = _name
        email = _email
        password = _password
        phone = _phone
        address = _address
        role = _role
        dob = _dob
    }

    constructor(map: HashMap<String, Any>) {
        val addrMap = map["address"]
        address = Address()
        addrMap?.let {
            if (it is HashMap<*, *>) {
                address = Address(it as HashMap<String, Any>)
            }
        }
        dob = Date()
        id = map.getOrDefault("id", "").toString()
        name = map.getOrDefault("name", "").toString()
        email = map.getOrDefault("email", "").toString()
        password = map.getOrDefault("password", "").toString()
        phone = map.getOrDefault("phone", "").toString()
        role = UserRole.valueOf(map.getOrDefault("role", "USER").toString())
        dob = Date(map.getOrDefault("dob", Date().unix()) as Long)
    }

    // Return true if user is admin
    val isAdmin: Boolean
        get() = role == UserRole.ADMIN

    // Get map representation of User
    fun map(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "password" to password,
            "phone" to phone,
            "address" to address.map(),
            "role" to role,
            "dob" to dob.unix(),
        )
    }
}