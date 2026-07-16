package com.example.scholarix.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scholarix.repo.ProfileRepoImpl
import com.example.scholarix.repo.UserRepoImpl
import com.example.scholarix.theme.Background
import com.example.scholarix.theme.DarkText
import com.example.scholarix.theme.PrimaryBlue
import com.example.scholarix.theme.ScholarixTheme
import com.example.scholarix.theme.SecondaryText
import com.example.scholarix.viewmodel.ProfileViewModel
import com.example.scholarix.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class StudentProfileActivity : ComponentActivity() {
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
        setContent {
            ScholarixTheme {
                StudentProfileScreen(userViewModel, profileViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            userViewModel.getUserById(uid)
            profileViewModel.getProfile(uid)
        } else {
            Toast.makeText(this, "User session not found. Please log in.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}


@Composable
fun StudentProfileScreen(
    userViewModel: UserViewModel,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val userModel by userViewModel.users.observeAsState(initial = null)
    val profileModel by profileViewModel.profile.observeAsState(initial = null)
    val isUserLoading by userViewModel.loading.observeAsState(initial = false)
    val isProfileLoading by profileViewModel.loading.observeAsState(initial = false)
    val isDeleting by userViewModel.deleteLoading.observeAsState(initial = false)

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        userViewModel.logout { success, message ->
                            if (success) {
                                val intent = Intent(context, LoginActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                context.startActivity(intent)
                                activity?.finish()
                            } else {
                                Toast.makeText(context, "Logout failed: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to permanently delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            userViewModel.deleteUser(uid) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    val intent = Intent(context, LoginActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                    context.startActivity(intent)
                                    activity?.finish()
                                }
                            }
                        } else {
                            Toast.makeText(context, "User session not found.", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (isDeleting) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Deleting Account") },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                    Text("Please wait...")
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        bottomBar = {
            StudentBottomNavigation(selected = BottomNavItem.Profile)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
        ) {
            if ((isUserLoading && userModel == null) || (isProfileLoading && profileModel == null)) {
                CircularProgressIndicator(
                    color = PrimaryBlue,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // 1. Large Greeting Section
                    val fullName = userModel?.fullName ?: ""
                    val firstName = fullName.trim().split(" ").firstOrNull() ?: ""
                    val firstLetter = if (fullName.isNotEmpty()) fullName.trim().first().toString().uppercase() else "?"
                    val email = userModel?.email ?: ""

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(PrimaryBlue, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = firstLetter,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (firstName.isNotEmpty()) "Hello, $firstName!" else "Hello!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        textAlign = TextAlign.Center
                    )

                    if (email.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = email,
                            fontSize = 14.sp,
                            color = SecondaryText,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 2. Profile Information card
                    Text(
                        text = "Student Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp)
                    )

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            ProfileInfoItem(
                                icon = Icons.Default.Person,
                                label = "Full Name",
                                value = userModel?.fullName
                            )
                            HorizontalDivider(color = Background.copy(alpha = 0.5f), thickness = 1.dp)

                            ProfileInfoItem(
                                icon = Icons.Default.Email,
                                label = "Email",
                                value = userModel?.email
                            )
                            HorizontalDivider(color = Background.copy(alpha = 0.5f), thickness = 1.dp)

                            ProfileInfoItem(
                                icon = Icons.Default.Phone,
                                label = "Phone Number",
                                value = userModel?.contact
                            )
                            HorizontalDivider(color = Background.copy(alpha = 0.5f), thickness = 1.dp)

                            ProfileInfoItem(
                                icon = Icons.Default.Home,
                                label = "Address",
                                value = profileModel?.address
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Background.copy(alpha = 0.5f),
                                thickness = 1.dp
                            )

                            Text(
                                text = "Academic Information",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            ProfileInfoItem(
                                icon = Icons.Default.Info,
                                label = "College",
                                value = profileModel?.college
                            )
                            HorizontalDivider(color = Background.copy(alpha = 0.5f), thickness = 1.dp)

                            ProfileInfoItem(
                                icon = Icons.Default.Info,
                                label = "Course",
                                value = profileModel?.course
                            )
                            HorizontalDivider(color = Background.copy(alpha = 0.5f), thickness = 1.dp)

                            ProfileInfoItem(
                                icon = Icons.Default.Info,
                                label = "CGPA",
                                value = profileModel?.cgpa
                            )
                            HorizontalDivider(color = Background.copy(alpha = 0.5f), thickness = 1.dp)

                            ProfileInfoItem(
                                icon = Icons.Default.Info,
                                label = "About Me",
                                value = profileModel?.description
                            )
                        }
                    }

                    // 3. Account section
                    Text(
                        text = "Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp)
                    )

                    AccountListItem(
                        icon = Icons.Default.Edit,
                        title = "Edit Profile",
                        onClick = {
                            val intent = Intent(context, CompleteProfileActivity::class.java).apply {
                                putExtra("edit_mode", true)
                            }
                            context.startActivity(intent)
                        }
                    )

                    AccountListItem(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        title = "Logout",
                        onClick = { showLogoutDialog = true }
                    )

                    AccountListItem(
                        icon = Icons.Default.Delete,
                        title = "Delete Account",
                        isDanger = true,
                        onClick = { showDeleteDialog = true }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = SecondaryText,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (value.isNullOrBlank()) "Not provided" else value,
                fontSize = 15.sp,
                color = DarkText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AccountListItem(
    icon: ImageVector,
    title: String,
    isDanger: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isDanger) Color.Red else PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDanger) Color.Red else DarkText,
                modifier = Modifier.weight(1f)
            )
        }
    }
}