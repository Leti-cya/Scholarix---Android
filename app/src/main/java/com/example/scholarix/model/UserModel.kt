package com.example.scholarix.model

data class UserModel(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val contact: String = "",
    val role: String = "",
    val profileCompleted: Boolean = false,

    val isVerified: Boolean = true,

    val profileImage: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "fullName" to fullName,
            "email" to email,
            "contact" to contact,
            "role" to role,
            "profileCompleted" to profileCompleted,
            "isVerified" to isVerified,
            "profileImage" to profileImage,
            "createdAt" to createdAt
        )
    }
}