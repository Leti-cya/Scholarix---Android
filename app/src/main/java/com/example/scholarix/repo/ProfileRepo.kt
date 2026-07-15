package com.example.scholarix.repo

import com.example.scholarix.model.ProfileModel

interface ProfileRepo {
    fun addProfile(
        id: String, model: ProfileModel,
        callback: (Boolean, String) -> Unit
    )

    fun getProfile(
        id: String,
        callback: (Boolean, String, ProfileModel?) -> Unit
    )

    fun updateProfile(
        id: String, model: ProfileModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteProfile(
        id: String,
        callback: (Boolean, String) -> Unit
    )

    fun getAllProfiles(
        callback: (Boolean, String, List<ProfileModel>) -> Unit
    )
}