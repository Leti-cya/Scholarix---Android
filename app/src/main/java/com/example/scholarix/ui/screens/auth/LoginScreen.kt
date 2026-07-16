package com.example.scholarix.ui.screens.auth

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
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scholarix.repository.UserRepoImpl
import com.example.scholarix.ui.components.PasswordField
import com.example.scholarix.ui.components.PrimaryButton
import com.example.scholarix.ui.components.TextField
import com.example.scholarix.ui.screens.common.CompleteProfileActivity
import com.example.scholarix.ui.screens.provider.ProviderDashboardActivity
import com.example.scholarix.ui.screens.student.StudentDashboardActivity
import com.example.scholarix.ui.theme.Background
import com.example.scholarix.ui.theme.PrimaryBlue
import com.example.scholarix.ui.theme.SecondaryText
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

    val context = LocalContext.current
    val activity = context as? Activity

    var isLoading by remember { mutableStateOf(false) }
    var navigated by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(color = Background)
            .padding(20.dp)
    ) {
        Spacer(
            modifier = Modifier.height(110.dp)
        )

        Text("Welcome Back",
            style = TextStyle(
                color = PrimaryBlue,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        Text("Glad to see you again :D",
            style = TextStyle(color = SecondaryText, textAlign = TextAlign.Center),
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(32.dp))

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

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text("Forgot Password?",
                style = TextStyle(color = PrimaryBlue, fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .testTag("forgotPassword")
                    .clickable {
                        val intent = Intent(context, ForgotPasswordActivity::class.java)
                        context.startActivity(intent)
                    }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Log In",
            isLoading = isLoading,
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                    return@PrimaryButton
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                    Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                    return@PrimaryButton
                }

                isLoading = true
                userViewModel.login(email.trim(), password) { success, message ->
                    if (success) {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            userViewModel.repo.getUserById(uid) { fetchSuccess, _, userModel ->
                                if (fetchSuccess && userModel != null) {
                                    if (!navigated) {
                                        navigated = true
                                        isLoading = false
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                                        activity?.finish()
                                    }
                                } else {
                                    isLoading = false
                                    FirebaseAuth.getInstance().signOut()
                                    Toast.makeText(context, "Your account data could not be found. Please register again or contact support.", Toast.LENGTH_LONG).show()
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
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text("Don't have an account? ")
            Text("Create one",
                modifier = Modifier
                    .testTag("register")
                    .clickable {
                        val intent = Intent(context, ChooseAccountActivity::class.java)
                        context.startActivity(intent)
                    },
                style = TextStyle(color = PrimaryBlue, fontWeight = FontWeight.Bold)
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