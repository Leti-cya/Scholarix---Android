package com.example.scholarix.model

data class ProfileModel(
    val uid: String = "",

    // Shared
    val name: String = "",
    val address: String = "",
    val description: String = "",

    // Student only
    val college: String = "",
    val course: String = "",
    val cgpa: String = "",

    // Provider only
    val organizationType: String = "",
    val website: String = "",

    val profileImage: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "address" to address,
            "description" to description,
            "college" to college,
            "course" to course,
            "cgpa" to cgpa,
            "organizationType" to organizationType,
            "website" to website,
            "profileImage" to profileImage
        )
    }
}