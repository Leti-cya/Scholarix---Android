package com.example.scholarix.ui.screens.auth

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scholarix.repository.UserRepoImpl
import com.example.scholarix.ui.components.BackButton
import com.example.scholarix.ui.components.PrimaryButton
import com.example.scholarix.ui.components.TextField
import com.example.scholarix.ui.components.TopBar
import com.example.scholarix.ui.theme.Background
import com.example.scholarix.ui.theme.PrimaryBlue
import com.example.scholarix.viewmodel.UserViewModel

import com.example.scholarix.ui.theme.SecondaryText

class ForgotPasswordActivity : ComponentActivity() {
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
            ForgotPasswordScreen(userViewModel)
        }
    }
}

@Composable
fun ForgotPasswordScreen(userViewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as? Activity

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
                text = "Forgot password?",
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
                text = "No worries, enter your email and we'll send you a reset link ;)",
                style = TextStyle(
                    color = SecondaryText,
                    textAlign = TextAlign.Center
                ),
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Enter your email"
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Send Reset Link",
                onClick = {
                    if (email.isBlank()) {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                        return@PrimaryButton
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                        return@PrimaryButton
                    }
                    userViewModel.forgotPassword(email.trim()) { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        if (success) {
                            activity?.finish()
                        }
                    }
                }
            )
        }
    }
}