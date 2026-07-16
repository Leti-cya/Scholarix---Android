package com.example.scholarix.ui.screens.auth

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.scholarix.model.UserModel
import com.example.scholarix.repository.UserRepoImpl
import com.example.scholarix.ui.components.BackButton
import com.example.scholarix.ui.components.PasswordField
import com.example.scholarix.ui.components.PrimaryButton
import com.example.scholarix.ui.components.TextField
import com.example.scholarix.ui.screens.common.CompleteProfileActivity
import com.example.scholarix.ui.theme.Background
import com.example.scholarix.ui.theme.PrimaryBlue
import com.example.scholarix.ui.theme.SecondaryText
import com.example.scholarix.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : ComponentActivity() {
    private lateinit var role: String

    private val userViewModel: UserViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(UserRepoImpl()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        role = intent.getStringExtra("ROLE") ?: "student"

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegisterScreen(
                userViewModel = userViewModel,
                role = role
            )
        }
    }
}

@Composable
fun RegisterScreen(
    userViewModel: UserViewModel,
    role: String
) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val activity = context as? Activity

    var isLoading by remember { mutableStateOf(false) }
    var navigated by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Spacer(Modifier.height(40.dp))

        BackButton()

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = if (role == "student")
                    "Create your Student Account"
                else
                    "Register as a Scholarship Provider",
                style = TextStyle(
                    color = PrimaryBlue,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "to join our family :3",
                style = TextStyle(
                    color = SecondaryText,
                    textAlign = TextAlign.Center
                ),
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = name,
                onValueChange = { name = it },
                label = if (role == "student") "Full Name" else "Organization Name",
                placeholder = if (role == "student") "Enter your full name" else "Enter organization name"
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = "Phone Number",
                placeholder = "Enter your phone number"
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Enter your email"
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Enter your password"
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Sign Up",
                isLoading = isLoading,
                onClick = {
                    if (name.isBlank() || mobile.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT)
                            .show()
                        return@PrimaryButton
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        Toast.makeText(
                            context,
                            "Please enter a valid email address",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@PrimaryButton
                    }
                    if (password.length < 6) {
                        Toast.makeText(
                            context,
                            "Password must be at least 6 characters long",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@PrimaryButton
                    }
                    if (mobile.trim().length < 7) {
                        Toast.makeText(
                            context,
                            "Please enter a valid contact number",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@PrimaryButton
                    }

                    isLoading = true
                    userViewModel.register(email.trim(), password) { success, message, userId ->
                        if (success) {
                            val userModel = UserModel(
                                id = userId,
                                fullName = name.trim(),
                                email = email.trim(),
                                contact = mobile.trim(),
                                role = role,
                                profileCompleted = false,
                                isVerified = true
                            )
                            userViewModel.addUser(userId, userModel) { dbSuccess, dbMsg ->
                                if (dbSuccess) {
                                    Toast.makeText(context, dbMsg, Toast.LENGTH_SHORT).show()
                                    if (!navigated) {
                                        navigated = true
                                        isLoading = false
                                        val intent =
                                            Intent(context, CompleteProfileActivity::class.java)
                                        context.startActivity(intent)
                                        activity?.finish()
                                    }
                                } else {
                                    isLoading = false
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    if (currentUser != null) {
                                        currentUser.delete().addOnCompleteListener { task ->
                                            FirebaseAuth.getInstance().signOut()

                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Registration failed. Your account has been rolled back.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Registration failed. Please contact support.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    } else {
                                        FirebaseAuth.getInstance().signOut()
                                        Toast.makeText(
                                            context,
                                            "Registration failed: database error.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            isLoading = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Text("Already have an account? ")
                Text(
                    "Log In",
                    modifier = Modifier.clickable {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        activity?.finish()
                    },
                    style = TextStyle(color = PrimaryBlue, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}