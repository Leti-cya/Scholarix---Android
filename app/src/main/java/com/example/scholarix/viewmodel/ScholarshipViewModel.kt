package com.example.scholarix.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scholarix.model.ScholarshipModel
import com.example.scholarix.repo.ScholarshipRepo

class ScholarshipViewModel(val repo: ScholarshipRepo) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    private val _scholarship = MutableLiveData<ScholarshipModel?>()
    val scholarship: MutableLiveData<ScholarshipModel?> get() = _scholarship

    private val _scholarshipList = MutableLiveData<List<ScholarshipModel>>()
    val scholarshipList: MutableLiveData<List<ScholarshipModel>> get() = _scholarshipList

    fun addScholarship(
        model: ScholarshipModel,
        callback: (Boolean, String) -> Unit
    ) {
        _loading.value = true
        repo.addScholarship(model) { success, message ->
            _loading.value = false
            callback(success, message)
        }
    }

    fun getScholarship(id: String) {
        _loading.value = true
        repo.getScholarship(id) { success, message, data ->
            if (success) {
                _scholarship.value = data
            } else {
                _scholarship.value = null
            }
            _loading.value = false
        }
    }

    fun getAllScholarships() {
        _loading.value = true
        repo.getAllScholarships { success, message, data ->
            if (success) {
                _scholarshipList.value = data
            } else {
                _scholarshipList.value = emptyList()
            }
            _loading.value = false
        }
    }

    fun getScholarshipsByProvider(providerId: String) {
        _loading.value = true
        repo.getScholarshipsByProvider(providerId) { success, message, data ->
            if (success) {
                _scholarshipList.value = data
            } else {
                _scholarshipList.value = emptyList()
            }
            _loading.value = false
        }
    }

    fun updateScholarship(
        id: String, model: ScholarshipModel,
        callback: (Boolean, String) -> Unit
    ) {
        _loading.value = true
        repo.updateScholarship(id, model) { success, message ->
            _loading.value = false
            callback(success, message)
        }
    }

    fun deleteScholarship(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        _loading.value = true
        repo.deleteScholarship(id) { success, message ->
            _loading.value = false
            callback(success, message)
        }
    }
}