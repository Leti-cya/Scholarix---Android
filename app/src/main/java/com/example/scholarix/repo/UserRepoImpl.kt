package com.example.scholarix.repo

import com.example.scholarix.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl : UserRepo {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("users")

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Login success")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Reset link sent to $email")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun getUserById(
        id: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        ref.child(id).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val user =
                    snapshot.getValue(UserModel::class.java)

                callback(
                    true,
                    "User fetched",
                    user
                )
            } else {
                callback(
                    false,
                    "User not found",
                    null
                )
            }
        }.addOnFailureListener {
            callback(
                false,
                it.message ?: "Unknown error",
                null
            )
        }
    }

    override fun getAllUsers(callback: (Boolean, String, List<UserModel?>) -> Unit) {
        ref.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val allUsers = mutableListOf<UserModel>()

                    for(user in snapshot.children){
                        val data = user.getValue(UserModel::class.java)

                        if(data != null){
                            allUsers.add(data)
                        }
                    }

                    callback(true,"fetched",allUsers)
                } else {
                    callback(false, "No users found", emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false,error.message,emptyList())
            }
        })
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logout successful")
        } catch (e: Exception) {
            callback(false, e.toString())

        }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Registration successful", "${auth.currentUser?.uid}")
                } else {
                    callback(false, "${it.exception?.message}", "")
                }
            }
    }

    override fun addUser(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        //to auto generate id
//        val id = ref.push().key.toString()
        ref.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User registered")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }
    }

    override fun editProfile(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).updateChildren(model.toMap()).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Profile updated")
            } else{
                callback(false,"${it.exception?.message}")

            }
        }

    }

    override fun deleteUser(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "User session not found. Please log in again.")
            return
        }

        // 1. Fetch user role and associated scholarships first without making any modifications
        fetchUserRoleAndScholarships(id,
            onComplete = { role, scholarshipKeys ->
                // 2. Attempt authentication deletion first
                currentUser.delete().addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        // 3. If auth deletion succeeds, delete database records
                        deleteDatabaseRecords(id, role, scholarshipKeys) { dbSuccess, dbMessage ->
                            if (dbSuccess) {
                                callback(true, "Account deleted successfully")
                            } else {
                                callback(false, dbMessage)
                            }
                        }
                    } else {
                        // 4. If auth deletion fails, do not delete any database records
                        val exception = authTask.exception
                        if (exception is com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException) {
                            callback(false, "Re-authentication required. Please log in again to delete your account.")
                        } else {
                            callback(false, exception?.message ?: "Failed to delete authentication account")
                        }
                    }
                }
            },
            onFailure = { errorMsg ->
                callback(false, errorMsg)
            }
        )
    }

    private fun fetchUserRoleAndScholarships(
        id: String,
        onComplete: (role: String, scholarshipKeys: List<String>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ref.child(id).get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                onComplete("", emptyList())
                return@addOnSuccessListener
            }
            val user = snapshot.getValue(UserModel::class.java)
            val role = user?.role ?: ""

            if (role == "provider") {
                val scholarshipsRef = database.getReference("scholarships")
                scholarshipsRef.orderByChild("providerId").equalTo(id).get()
                    .addOnSuccessListener { scholarshipsSnapshot ->
                        val keys = mutableListOf<String>()
                        if (scholarshipsSnapshot.exists()) {
                            for (child in scholarshipsSnapshot.children) {
                                val key = child.key
                                if (key != null) {
                                    keys.add(key)
                                }
                            }
                        }
                        onComplete(role, keys)
                    }
                    .addOnFailureListener {
                        onFailure(it.message ?: "Failed to fetch scholarships")
                    }
            } else {
                onComplete(role, emptyList())
            }
        }.addOnFailureListener {
            onFailure(it.message ?: "Failed to fetch user information")
        }
    }

    private fun deleteDatabaseRecords(
        id: String,
        role: String,
        scholarshipKeys: List<String>,
        onComplete: (Boolean, String) -> Unit
    ) {
        val deleteTasks = mutableListOf<com.google.android.gms.tasks.Task<Void>>()

        // Delete user record from users/{uid}
        deleteTasks.add(ref.child(id).removeValue())

        // Delete profile from profiles/{uid}
        deleteTasks.add(database.getReference("profiles").child(id).removeValue())

        // Delete scholarships if user is a provider
        if (role == "provider") {
            val scholarshipsRef = database.getReference("scholarships")
            for (key in scholarshipKeys) {
                deleteTasks.add(scholarshipsRef.child(key).removeValue())
            }
        }

        com.google.android.gms.tasks.Tasks.whenAllComplete(deleteTasks)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, "All database records deleted successfully")
                } else {
                    onComplete(false, task.exception?.message ?: "Failed to delete some database records")
                }
            }
    }

    override fun changePassword(
        currentPassword: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "User session not found. Please log in again.")
            return
        }

        val email = currentUser.email
        if (email.isNullOrEmpty()) {
            callback(false, "User email not found.")
            return
        }

        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        currentUser.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    currentUser.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                callback(true, "Password updated successfully.")
                            } else {
                                callback(false, updateTask.exception?.message ?: "Failed to update password.")
                            }
                        }
                } else {
                    val exception = reauthTask.exception
                    if (exception is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                        callback(false, "Current password is incorrect.")
                    } else {
                        callback(false, exception?.message ?: "Current password is incorrect.")
                    }
                }
            }
    }
}