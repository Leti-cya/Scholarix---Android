package com.example.scholarix.ui.screens.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scholarix.model.ProfileModel
import com.example.scholarix.repository.ProfileRepoImpl
import com.example.scholarix.repository.UserRepoImpl
import com.example.scholarix.ui.components.PrimaryButton
import com.example.scholarix.ui.components.TopBar
import com.example.scholarix.ui.components.TextField
import com.example.scholarix.ui.screens.auth.LoginActivity
import com.example.scholarix.ui.screens.provider.ProviderDashboardActivity
import com.example.scholarix.ui.screens.student.StudentDashboardActivity
import com.example.scholarix.ui.theme.Background
import com.example.scholarix.ui.theme.PrimaryBlue
import com.example.scholarix.ui.theme.SecondaryText
import com.example.scholarix.ui.theme.ScholarixTheme
import com.example.scholarix.viewmodel.ProfileViewModel
import com.example.scholarix.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class CompleteProfileActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(UserRepoImpl()) as T
            }
        }
    }

    private val profileViewModel: ProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(ProfileRepoImpl()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            userViewModel.getUserById(uid)
        } else {
            Toast.makeText(this, "User session not found. Please log in.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val isEditMode = intent.getBooleanExtra("edit_mode", false)
        if (uid != null) {
            profileViewModel.getProfile(uid)
        }

        setContent {
            ScholarixTheme {
                CompleteProfileScreen(userViewModel, profileViewModel, uid ?: "", isEditMode)
            }
        }
    }
}

@Composable
fun CompleteProfileScreen(
    userViewModel: UserViewModel,
    profileViewModel: ProfileViewModel,
    uid: String,
    isEditMode: Boolean = false
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val userModel by userViewModel.users.observeAsState(initial = null)

    val isUserLoading by userViewModel.loading.observeAsState(initial = false)
    val isProfileSaving by profileViewModel.loading.observeAsState(initial = false)

    // Form inputs
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Student fields
    var college by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var cgpa by remember { mutableStateOf("") }

    // Provider fields
    var organizationType by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }

    // Prefill full name when userModel changes
    LaunchedEffect(userModel) {
        userModel?.let {
            if (name.isEmpty()) {
                name = it.fullName
            }
        }
    }

    val profileModel by profileViewModel.profile.observeAsState(initial = null)

    // Prefill other profile fields when profileModel changes
    LaunchedEffect(profileModel) {
        profileModel?.let {
            if (address.isEmpty()) address = it.address
            if (description.isEmpty()) description = it.description
            if (college.isEmpty()) college = it.college
            if (course.isEmpty()) course = it.course
            if (cgpa.isEmpty()) cgpa = it.cgpa
            if (organizationType.isEmpty()) organizationType = it.organizationType
            if (website.isEmpty()) website = it.website
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
    ) {
        if (isUserLoading || userModel == null) {
            CircularProgressIndicator(
                color = PrimaryBlue,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            val role = userModel?.role ?: "student"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (isEditMode) {
                    TopBar(
                        title = "Edit Profile",
                        showBackButton = true
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!isEditMode) {
                        Spacer(modifier = Modifier.height(110.dp))

                        Text(
                            text = "Complete Profile",
                            style = TextStyle(
                                color = PrimaryBlue,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Please fill out the details below to complete your profile :}",
                            style = TextStyle(
                                color = SecondaryText,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Role-specific fields
                    if (role == "student") {
                        TextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Full Name",
                            placeholder = "Enter your full name"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = address,
                            onValueChange = { address = it },
                            label = "Address",
                            placeholder = "Enter your address"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = college,
                            onValueChange = { college = it },
                            label = "College",
                            placeholder = "Enter your college/university name"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = course,
                            onValueChange = { course = it },
                            label = "Course",
                            placeholder = "Enter your course/degree"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = cgpa,
                            onValueChange = { cgpa = it },
                            label = "CGPA",
                            placeholder = "Enter your CGPA"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "About Me",
                            placeholder = "Tell us about yourself"
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    } else {
                        TextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Organization Name",
                            placeholder = "Enter organization name"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = organizationType,
                            onValueChange = { organizationType = it },
                            label = "Organization Type",
                            placeholder = "Enter organization type"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = address,
                            onValueChange = { address = it },
                            label = "Address",
                            placeholder = "Enter your address"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = website,
                            onValueChange = { website = it },
                            label = "Website",
                            placeholder = "Enter website link"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Organization Description",
                            placeholder = "Describe your organization"
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Save button
                    PrimaryButton(
                        text = if (isEditMode) "Save Changes" else "Complete Profile",
                        isLoading = isProfileSaving,
                        onClick = {
                            if (role == "student") {
                                if (
                                    name.isBlank() ||
                                    address.isBlank() ||
                                    college.isBlank() ||
                                    course.isBlank() ||
                                    cgpa.isBlank() ||
                                    description.isBlank()
                                ) {
                                    Toast.makeText(
                                        context,
                                        "Please fill in all fields",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@PrimaryButton
                                }
                            } else {
                                if (
                                    name.isBlank() ||
                                    organizationType.isBlank() ||
                                    address.isBlank() ||
                                    website.isBlank() ||
                                    description.isBlank()
                                ) {
                                    Toast.makeText(
                                        context,
                                        "Please fill in all fields",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@PrimaryButton
                                }
                            }

                            val profileModel = ProfileModel(
                                uid = uid,
                                name = name.trim(),
                                address = address.trim(),
                                description = description.trim(),

                                college = if (role == "student") college.trim() else "",
                                course = if (role == "student") course.trim() else "",
                                cgpa = if (role == "student") cgpa.trim() else "",

                                organizationType =
                                    if (role == "provider") organizationType.trim() else "",

                                website =
                                    if (role == "provider") website.trim() else ""
                            )

                            profileViewModel.addProfile(uid, profileModel) { profileSuccess, profileMsg ->
                                if (profileSuccess) {
                                    val currentModel = userModel
                                    if (currentModel != null) {
                                        val updatedUser = currentModel.copy(
                                            fullName = name.trim(),
                                            profileCompleted = true
                                        )
                                        userViewModel.editProfile(uid, updatedUser) { userSuccess, userMsg ->
                                            if (userSuccess) {
                                                if (isEditMode) {
                                                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                                    activity?.finish()
                                                } else {
                                                    Toast.makeText(context, "Profile completed successfully!", Toast.LENGTH_SHORT).show()
                                                    if (role == "student") {
                                                        context.startActivity(
                                                            Intent(context, StudentDashboardActivity::class.java)
                                                        )
                                                    } else {
                                                        context.startActivity(
                                                            Intent(context, ProviderDashboardActivity::class.java)
                                                        )
                                                    }
                                                    activity?.finish()
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Saved profile but failed to update status: $userMsg",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to save profile: $profileMsg",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}