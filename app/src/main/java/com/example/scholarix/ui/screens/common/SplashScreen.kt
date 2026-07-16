package com.example.scholarix.ui.screens.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scholarix.R
import com.example.scholarix.repository.UserRepoImpl
import com.example.scholarix.ui.screens.auth.LoginActivity
import com.example.scholarix.ui.screens.provider.ProviderDashboardActivity
import com.example.scholarix.ui.screens.student.StudentDashboardActivity
import com.example.scholarix.ui.theme.Background
import com.example.scholarix.ui.theme.PrimaryBlue
import com.example.scholarix.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
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
            SplashScreen(userViewModel)
        }
    }
}

@Composable
fun SplashScreen(userViewModel: UserViewModel) {
    val context = LocalContext.current
    val activity = context as Activity

    var visible by remember {
        mutableStateOf(false)
    }

    var navigated by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        visible = true
        delay(1500)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            userViewModel.repo.getUserById(user.uid) { success, _, userModel ->
                if (!navigated) {
                    navigated = true
                    if (success && userModel != null) {
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
                        // User exists in Auth but profile node in database is missing or incomplete
                        context.startActivity(Intent(context, CompleteProfileActivity::class.java))
                    }
                    activity.finish()
                }
            }
        } else {
            if (!navigated) {
                navigated = true
                context.startActivity(Intent(context, LoginActivity::class.java))
                activity.finish()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn()
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Scholarix",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Find Scholarships. Build Your Future.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(40.dp))

        CircularProgressIndicator(
            color = PrimaryBlue
        )
    }
}