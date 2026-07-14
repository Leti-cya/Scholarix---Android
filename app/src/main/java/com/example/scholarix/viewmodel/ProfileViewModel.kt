package com.example.scholarix.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scholarix.model.ProfileModel
import com.example.scholarix.repo.ProfileRepo

class ProfileViewModel(val repo: ProfileRepo) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    private val _profile = MutableLiveData<ProfileModel?>()
    val profile: MutableLiveData<ProfileModel?> get() = _profile

    fun addProfile(
        id: String, model: ProfileModel,
        callback: (Boolean, String) -> Unit
    ) {
        _loading.value = true
        repo.addProfile(id, model) { success, message ->
            _loading.value = false
            callback(success, message)
        }
    }

    fun getProfile(id: String) {
        _loading.value = true
        repo.getProfile(id) { success, message, data ->
            if (success) {
                _profile.value = data
                _loading.value = false
            } else {
                _profile.value = null
                _loading.value = false
            }
        }
    }

    fun updateProfile(
        id: String, model: ProfileModel,
        callback: (Boolean, String) -> Unit
    ) {
        _loading.value = true
        repo.updateProfile(id, model) { success, message ->
            _loading.value = false
            callback(success, message)
        }
    }

    fun deleteProfile(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        _loading.value = true
        repo.deleteProfile(id) { success, message ->
            _loading.value = false
            callback(success, message)
        }
    }
}