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
import androidx.compose.ui.unit.dp
import com.example.scholarix.theme.Background

class StudentDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentDashboardScreen()
        }
    }
}

@Composable
fun StudentDashboardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
            .padding(20.dp)
    ) {
        Text("Welcome to Student Dashboard")
    }
}