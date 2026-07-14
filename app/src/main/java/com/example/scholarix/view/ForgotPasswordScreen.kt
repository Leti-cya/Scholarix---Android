package com.example.scholarix.view

import android.app.Activity
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scholarix.repo.UserRepoImpl
import com.example.scholarix.theme.Background
import com.example.scholarix.theme.PrimaryBlue
import com.example.scholarix.viewmodel.UserViewModel

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
        modifier = Modifier.fillMaxSize()
            .background(color = Background)
            .padding(20.dp)
    ) {
        Spacer(
            modifier = Modifier.height(100.dp)
        )

        Text(
            "Forgot password?", style = TextStyle(
                color = PrimaryBlue,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            "No worries, enter your email and we'll send you a reset link ;)",
            style = TextStyle(textAlign = TextAlign.Center),
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = {
                    if (email.isBlank()) {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    userViewModel.forgotPassword(email.trim()) { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        if (success) {
                            activity?.finish()
                        }
                    }
                },
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Send Reset Link")
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text("Go back to ")
            Text("login.",
                modifier = Modifier.clickable {
                    activity?.finish()
                },
                style = TextStyle(color = PrimaryBlue))
        }
    }
}