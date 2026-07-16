package com.example.scholarix.repo

import com.example.scholarix.model.UserModel

interface UserRepo {
    fun login(
        email: String, password: String,
        callback: (Boolean, String) -> Unit
    )

//    {
//    "success": true
//     "message": "OTP link has been sent to your email"
//    }

    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    )

//    {
//    "success": true
//     "message": "all user fetched"
//    }
    fun getUserById(
        id: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )

    fun getAllUsers(
        callback: (Boolean, String, List<UserModel?>) -> Unit
    )

    fun logout(
        callback: (Boolean, String) -> Unit
    )

//    {
//    "success": true
//    "message": "registered",
//    "userId": "fsdgdfhrtserawedsfdg"
//    }
    // Firebase Authentication
    fun register(
        email: String, password: String,
        callback: (Boolean, String, String) -> Unit
    )

//    {
//    "success": true
//     "message": "registered",
//    }
    //real-time database
    fun addUser(
        id: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun editProfile(
        id: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteUser(
        id: String,
        callback: (Boolean, String) -> Unit
    )

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    )
}