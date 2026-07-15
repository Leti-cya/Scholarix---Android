package com.example.scholarix.repo

import com.example.scholarix.model.ScholarshipModel

interface ScholarshipRepo {
    fun addScholarship(
        model: ScholarshipModel,
        callback: (Boolean, String) -> Unit
    )

    fun getScholarship(
        id: String,
        callback: (Boolean, String, ScholarshipModel?) -> Unit
    )

    fun getAllScholarships(
        callback: (Boolean, String, List<ScholarshipModel>) -> Unit
    )

    fun getScholarshipsByProvider(
        providerId: String,
        callback: (Boolean, String, List<ScholarshipModel>) -> Unit
    )

    fun updateScholarship(
        id: String, model: ScholarshipModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteScholarship(
        id: String,
        callback: (Boolean, String) -> Unit
    )
}