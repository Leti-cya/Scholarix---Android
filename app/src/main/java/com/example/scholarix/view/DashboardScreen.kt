package com.example.scholarix.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarix.ui.theme.Background

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardScreen()
        }
    }
}

@Composable
fun DashboardScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(color = Background)
            .padding(20.dp)
    ) {
        Text("Welcome to my dashboard")
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    DashboardScreen()
}