package com.example.scholarix.ui.screens.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scholarix.ui.theme.Background
import com.example.scholarix.ui.theme.PrimaryBlue

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.scholarix.ui.components.BackButton
import com.example.scholarix.ui.components.TopBar
import com.example.scholarix.ui.theme.SecondaryText

class ChooseAccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChooseAccountScreen()
        }
    }
}

@Composable
fun ChooseAccountScreen() {
    val context = LocalContext.current
    val activity = context as Activity

    Column(
        modifier = Modifier.fillMaxSize()
            .background(color = Background)
            .padding(20.dp)
    ) {
        Spacer(
            modifier = Modifier.height(110.dp)
        )
        Text(
            text = "Create Account",
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
            text = "Choose how you want to register :)",
            style = TextStyle(
                color = SecondaryText,
                textAlign = TextAlign.Center
            ),
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        AccountCard(
            title = "Student",
            subtitle = "Search and apply for scholarships",
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(48.dp)
                )
            }
        ) {
            val intent = Intent(context, RegisterActivity::class.java)
            intent.putExtra("ROLE", "student")
            context.startActivity(intent)
        }

        Spacer(modifier = Modifier.height(24.dp))

        AccountCard(
            title = "Provider",
            subtitle = "Publish and manage scholarships",
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(48.dp)
                )
            }
        ) {
            val intent = Intent(context, RegisterActivity::class.java)
            intent.putExtra("ROLE", "provider")
            context.startActivity(intent)
        }

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
                    activity.finish()
                },
                style = TextStyle(color = PrimaryBlue, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun AccountCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                subtitle,
                color = Color.Gray
            )
        }
    }
}