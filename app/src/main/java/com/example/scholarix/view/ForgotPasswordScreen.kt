package com.example.scholarix.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scholarix.ui.theme.Background
import com.example.scholarix.ui.theme.PrimaryBlue

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgotPasswordScreen()
        }
    }
}

@Composable
fun ForgotPasswordScreen() {
    var email by remember { mutableStateOf("") }

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
                modifier = Modifier.clickable{},
                style = TextStyle(color = PrimaryBlue))
        }
    }
}

@Preview
@Composable
fun ForgotPasswordPreview() {
    ForgotPasswordScreen()
}