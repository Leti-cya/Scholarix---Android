package com.example.scholarix.ui.screens.common

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scholarix.repository.UserRepoImpl
import com.example.scholarix.ui.components.PasswordField
import com.example.scholarix.ui.components.PrimaryButton
import com.example.scholarix.ui.components.TopBar
import com.example.scholarix.ui.theme.Background
import com.example.scholarix.ui.theme.SecondaryText
import com.example.scholarix.viewmodel.UserViewModel

class ChangePasswordActivity : ComponentActivity() {
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
            ChangePasswordScreen(userViewModel)
        }
    }
}

@Composable
fun ChangePasswordScreen(userViewModel: UserViewModel) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
    ) {
        TopBar(
            title = "Change Password",
            showBackButton = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = "Please enter your current password and specify a new password.",
                style = TextStyle(color = SecondaryText),
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            PasswordField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = "Current Password",
                placeholder = "Enter current password"
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "New Password",
                placeholder = "Enter new password"
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                label = "Confirm New Password",
                placeholder = "Confirm new password"
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Update Password",
                isLoading = isLoading,
                onClick = {
                    if (currentPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank()) {
                        Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                        return@PrimaryButton
                    }
                    if (newPassword.length < 6) {
                        Toast.makeText(context, "New password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                        return@PrimaryButton
                    }
                    if (newPassword != confirmNewPassword) {
                        Toast.makeText(context, "New password and confirm password do not match", Toast.LENGTH_SHORT).show()
                        return@PrimaryButton
                    }
                    if (newPassword == currentPassword) {
                        Toast.makeText(context, "New password must be different from current password", Toast.LENGTH_SHORT).show()
                        return@PrimaryButton
                    }

                    isLoading = true
                    userViewModel.changePassword(currentPassword, newPassword) { success, message ->
                        isLoading = false
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