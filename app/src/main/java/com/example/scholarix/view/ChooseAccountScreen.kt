package com.example.scholarix.view

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
import androidx.compose.material3.MaterialTheme
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
import com.example.scholarix.theme.Background
import com.example.scholarix.theme.PrimaryBlue

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
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(
            "Create Account",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "Choose how you want to register",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(50.dp))

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

        Spacer(modifier = Modifier.height(25.dp))

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

        Text(
            "Already have an account?",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Login",
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                activity.finish()
            }
        )

        Spacer(modifier = Modifier.height(30.dp))
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