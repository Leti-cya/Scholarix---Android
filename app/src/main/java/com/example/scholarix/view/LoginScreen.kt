package com.example.scholarix.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth
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
import com.example.scholarix.repo.UserRepoImpl
import com.example.scholarix.theme.Background
import com.example.scholarix.theme.PrimaryBlue
import com.example.scholarix.viewmodel.UserViewModel

class LoginActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(UserRepoImpl()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginScreen(userViewModel)
        }
    }
}

@Composable
fun LoginScreen(userViewModel: UserViewModel) {
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

        Text("Welcome back!", style = TextStyle(
            color = PrimaryBlue,
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth())

        Text("Glad to see you again :D",
            style = TextStyle(textAlign = TextAlign.Center),
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth())

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
            label = {
                Text("email")
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
            label = {
                Text("password")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Gray.copy(alpha = 0.1f),
                focusedContainerColor = Color.Blue
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text("Forgot Password?",
                style = TextStyle(color = PrimaryBlue),
                modifier = Modifier.clickable {
                    val intent = Intent(context, ForgotPasswordActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }

        var isLoading by remember { mutableStateOf(false) }
        var navigated by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    userViewModel.login(email.trim(), password) { success, message ->
                        if (success) {
                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (uid != null) {
                                userViewModel.repo.getUserById(uid) { fetchSuccess, _, userModel ->
                                    if (!navigated) {
                                        navigated = true
                                        isLoading = false
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        if (fetchSuccess && userModel != null) {
                                            if (userModel.profileCompleted) {
                                                if (userModel.role == "student") {
                                                    context.startActivity(
                                                        Intent(
                                                            context,
                                                            StudentDashboardActivity::class.java
                                                        )
                                                    )
                                                } else {
                                                    context.startActivity(
                                                        Intent(
                                                            context,
                                                            ProviderDashboardActivity::class.java
                                                        )
                                                    )
                                                }
                                            }
                                            else {
                                                context.startActivity(
                                                    Intent(
                                                        context,
                                                        CompleteProfileActivity::class.java
                                                    )
                                                )
                                            }
                                        } else {
                                            // Handle missing profile
                                            context.startActivity(Intent(context, CompleteProfileActivity::class.java))
                                        }
                                        activity?.finish()
                                    }
                                }
                            } else {
                                isLoading = false
                                Toast.makeText(context, "User session not found", Toast.LENGTH_SHORT).show()
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
                    Text(text = "Log In")
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

            Text("Or Login with", modifier = Modifier.padding(horizontal = 5.dp))

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 2.dp,
                color = Color.Gray.copy(alpha = 0.3f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            LoginCard(
                modifier = Modifier.fillMaxWidth()
                    .height(50.dp)
                    .weight(1f),
                image = R.drawable.google,
                label = "Google")
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text("Don't have account? ")
            Text("Sign up.",
                modifier = Modifier.clickable {
                    val intent = Intent(context, ChooseAccountActivity::class.java)
                    context.startActivity(intent)
                },
                style = TextStyle(color = PrimaryBlue)
            )
        }
    }
}

@Composable
fun LoginCard(
    modifier: Modifier,
    image : Int,
    label : String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = "facebook",
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(label)
        }
    }
}