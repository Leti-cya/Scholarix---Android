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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scholarix.R
import com.example.scholarix.model.UserModel
import com.example.scholarix.repo.UserRepoImpl
import com.example.scholarix.theme.Background
import com.example.scholarix.theme.PrimaryBlue
import com.example.scholarix.view.LoginScreen
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

    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    Column(
        modifier = Modifier.fillMaxSize()
            .background(color = Background)
            .padding(20.dp)
    ) {
        Spacer(
            modifier = Modifier.height(100.dp)
        )

        Text(text = if (role == "student")
                "Create your Student Account"
            else
                "Register as a Scholarship Provider",
            style = TextStyle(
                color = PrimaryBlue,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth())

        Text("to join our family :3",
            style = TextStyle(textAlign = TextAlign.Center),
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth())

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            placeholder = {
                Text(
                    if (role == "student")
                        "Enter your full name"
                    else
                        "Enter organization name"
                )
            },
            label = {
                Text(
                    if (role == "student")
                        "Full Name"
                    else
                        "Organization Name"
                )
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Gray.copy(alpha = 0.1f),
                focusedContainerColor = Color.Blue
            )
        )

        OutlinedTextField(
            value = mobile,
            onValueChange = {
                mobile = it
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Enter your phone number")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Gray.copy(alpha = 0.1f),
                focusedContainerColor = Color.Blue
            )
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 15.dp),
            placeholder = {
                Text("Enter your email")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Gray.copy(alpha = 0.1f),
                focusedContainerColor = Color.Blue
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            visualTransformation =
                if (visibility)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

            trailingIcon = {
                IconButton(onClick = {
                    visibility = !visibility
                }) {
                    Icon(
                        painter =
                            if (visibility)
                                painterResource(R.drawable.visibility_on)
                            else
                                painterResource(R.drawable.visibility_off),
                        contentDescription = null
                    )
                }
            },

            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("*******************")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Gray.copy(alpha = 0.1f),
                focusedContainerColor = Color.Blue
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        var isLoading by remember { mutableStateOf(false) }
        var navigated by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = {
                    if (name.isBlank() || mobile.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password.length < 6) {
                        Toast.makeText(context, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (mobile.trim().length < 7) {
                        Toast.makeText(context, "Please enter a valid contact number", Toast.LENGTH_SHORT).show()
                        return@Button
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
                                        val intent = Intent(context, CompleteProfileActivity::class.java)
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
                                        Toast.makeText(context, "Registration failed: database error.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        } else {
                            isLoading = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Sign Up")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 2.dp,
                color = Color.Gray.copy(alpha = 0.3f)
            )
            Text("Or Register With", modifier = Modifier.padding(horizontal = 5.dp))
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 2.dp,
                color = Color.Gray.copy(alpha = 0.3f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            LoginCard(
                modifier = Modifier.fillMaxWidth()
                    .height(50.dp)
                    .weight(1f),
                image = R.drawable.google,
                label = "Google"
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text("Already have an account? ")
            Text("Sign in.",
                modifier = Modifier.clickable {
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    activity?.finish()
                },
                style = TextStyle(color = PrimaryBlue))
        }
    }
}