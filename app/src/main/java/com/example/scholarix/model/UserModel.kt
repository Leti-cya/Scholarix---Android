package com.example.scholarix.model

data class UserModel(
    val id : String = "",
    val name : String = "",
    val email : String = "",
    val password : String = "",
    val address : String = "",
    val contact : String = "",
){
    fun toMap() : Map<String,Any?>{
        return mapOf(
            "name" to name,
            "email" to email,
            "password" to password,
            "address" to address,
            "contact" to contact,
        )
    }
}