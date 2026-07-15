package com.example.scholarix.repo

import com.example.scholarix.model.ScholarshipModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ScholarshipRepoImpl : ScholarshipRepo {
    private val database = FirebaseDatabase.getInstance()
    private val ref = database.getReference("scholarships")

    override fun addScholarship(
        model: ScholarshipModel,
        callback: (Boolean, String) -> Unit
    ) {
        val key = ref.push().key ?: return callback(false, "Failed to generate scholarship ID")
        val updatedModel = model.copy(id = key)
        ref.child(key).setValue(updatedModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Scholarship added successfully")
            } else {
                callback(false, it.exception?.message ?: "Unknown error occurred")
            }
        }
    }

    override fun getScholarship(
        id: String,
        callback: (Boolean, String, ScholarshipModel?) -> Unit
    ) {
        ref.child(id).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val scholarship = snapshot.getValue(ScholarshipModel::class.java)
                callback(true, "Scholarship fetched successfully", scholarship)
            } else {
                callback(false, "Scholarship not found", null)
            }
        }.addOnFailureListener {
            callback(false, it.message ?: "Unknown error occurred", null)
        }
    }

    override fun getAllScholarships(
        callback: (Boolean, String, List<ScholarshipModel>) -> Unit
    ) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ScholarshipModel>()
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val data = child.getValue(ScholarshipModel::class.java)
                        if (data != null) {
                            list.add(data)
                        }
                    }
                }
                callback(true, "Scholarships fetched successfully", list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun getScholarshipsByProvider(
        providerId: String,
        callback: (Boolean, String, List<ScholarshipModel>) -> Unit
    ) {
        ref.orderByChild("providerId").equalTo(providerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<ScholarshipModel>()
                    if (snapshot.exists()) {
                        for (child in snapshot.children) {
                            val data = child.getValue(ScholarshipModel::class.java)
                            if (data != null) {
                                list.add(data)
                            }
                        }
                    }
                    callback(true, "Scholarships fetched successfully", list)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun updateScholarship(
        id: String,
        model: ScholarshipModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Scholarship updated successfully")
            } else {
                callback(false, it.exception?.message ?: "Unknown error occurred")
            }
        }
    }

    override fun deleteScholarship(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Scholarship deleted successfully")
            } else {
                callback(false, it.exception?.message ?: "Unknown error occurred")
            }
        }
    }
}