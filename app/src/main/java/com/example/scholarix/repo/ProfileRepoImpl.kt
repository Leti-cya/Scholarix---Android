package com.example.scholarix.repo

import com.example.scholarix.model.ProfileModel
import com.google.firebase.database.FirebaseDatabase

class ProfileRepoImpl : ProfileRepo {
    private val database = FirebaseDatabase.getInstance()
    private val ref = database.getReference("profiles")

    override fun addProfile(
        id: String,
        model: ProfileModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Profile added successfully")
            } else {
                callback(false, it.exception?.message ?: "Unknown error occurred")
            }
        }
    }

    override fun getProfile(
        id: String,
        callback: (Boolean, String, ProfileModel?) -> Unit
    ) {
        ref.child(id).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val profile = snapshot.getValue(ProfileModel::class.java)
                callback(true, "Profile fetched successfully", profile)
            } else {
                callback(false, "Profile not found", null)
            }
        }.addOnFailureListener {
            callback(false, it.message ?: "Unknown error occurred", null)
        }
    }

    override fun updateProfile(
        id: String,
        model: ProfileModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Profile updated successfully")
            } else {
                callback(false, it.exception?.message ?: "Unknown error occurred")
            }
        }
    }

    override fun deleteProfile(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Profile deleted successfully")
            } else {
                callback(false, it.exception?.message ?: "Unknown error occurred")
            }
        }
    }
}