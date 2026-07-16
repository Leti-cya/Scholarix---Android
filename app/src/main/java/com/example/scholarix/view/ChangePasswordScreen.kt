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
import androidx.compose.foundation.layout.Column
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
import com.example.scholarix.repo.UserRepoImpl
import com.example.scholarix.theme.Background
import com.example.scholarix.theme.PrimaryBlue
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

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmNewPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Change Password",
            style = TextStyle(
                color = PrimaryBlue,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Please enter your current password and specify a new password.",
            style = TextStyle(textAlign = TextAlign.Center, color = Color.Gray),
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 24.dp)
        )

        // 1. Current Password Field
        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                    Icon(
                        painter = if (currentPasswordVisible)
                            painterResource(R.drawable.visibility_on)
                        else
                            painterResource(R.drawable.visibility_off),
                        contentDescription = null
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter current password") },
            label = { Text("Current Password") },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = PrimaryBlue.copy(alpha = 0.3f),
                focusedContainerColor = Color.Gray.copy(alpha = 0.05f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. New Password Field
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                    Icon(
                        painter = if (newPasswordVisible)
                            painterResource(R.drawable.visibility_on)
                        else
                            painterResource(R.drawable.visibility_off),
                        contentDescription = null
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter new password") },
            label = { Text("New Password") },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = PrimaryBlue.copy(alpha = 0.3f),
                focusedContainerColor = Color.Gray.copy(alpha = 0.05f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Confirm New Password Field
        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
            visualTransformation = if (confirmNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmNewPasswordVisible = !confirmNewPasswordVisible }) {
                    Icon(
                        painter = if (confirmNewPasswordVisible)
                            painterResource(R.drawable.visibility_on)
                        else
                            painterResource(R.drawable.visibility_off),
                        contentDescription = null
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Confirm new password") },
            label = { Text("Confirm New Password") },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = PrimaryBlue.copy(alpha = 0.3f),
                focusedContainerColor = Color.Gray.copy(alpha = 0.05f)
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 4. Update Password Button
        Button(
            onClick = {
                if (currentPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank()) {
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (newPassword.length < 6) {
                    Toast.makeText(context, "New password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (newPassword != confirmNewPassword) {
                    Toast.makeText(context, "New password and confirm password do not match", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (newPassword == currentPassword) {
                    Toast.makeText(context, "New password must be different from current password", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true
                userViewModel.changePassword(currentPassword, newPassword) { success, message ->
                    isLoading = false
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        activity?.finish()
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                contentColor = Color.White
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Update Password",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cancel / Back text button
        Text(
            text = "Cancel",
            modifier = Modifier.clickable {
                activity?.finish()
            },
            style = TextStyle(
                color = PrimaryBlue,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        )
    }
}