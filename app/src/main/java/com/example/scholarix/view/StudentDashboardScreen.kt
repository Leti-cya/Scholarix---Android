package com.example.scholarix.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scholarix.model.ScholarshipModel
import com.example.scholarix.repo.ScholarshipRepoImpl
import com.example.scholarix.repo.UserRepoImpl
import com.example.scholarix.theme.Background
import com.example.scholarix.theme.DarkText
import com.example.scholarix.theme.PrimaryBlue
import com.example.scholarix.theme.ScholarixTheme
import com.example.scholarix.theme.SecondaryText
import com.example.scholarix.viewmodel.ScholarshipViewModel
import com.example.scholarix.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class StudentDashboardActivity : ComponentActivity() {
    private val scholarshipViewModel: ScholarshipViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ScholarshipViewModel(ScholarshipRepoImpl()) as T
            }
        }
    }

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

        scholarshipViewModel.getAllScholarships()

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            userViewModel.getUserById(it)
        }

        setContent {
            ScholarixTheme {
                StudentDashboardScreen(
                    scholarshipViewModel,
                    userViewModel
                )
            }
        }
    }
}

@Composable
fun StudentDashboardScreen(
    scholarshipViewModel: ScholarshipViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val scholarshipList by scholarshipViewModel.scholarshipList.observeAsState(initial = emptyList())
    val isLoading by scholarshipViewModel.loading.observeAsState(initial = false)

    val userModel by userViewModel.users.observeAsState(initial = null)
    val firstName =
        userModel?.fullName
            ?.trim()
            ?.split(" ")
            ?.firstOrNull()
            ?: ""

    Scaffold(
        bottomBar = {
            StudentBottomNavigation(selected = BottomNavItem.Home)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Background)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Dashboard Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {

                    Text(
                        text = if (firstName.isNotBlank())
                            "Hello, $firstName!"
                        else
                            "Hello!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Browse Scholarships",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Find opportunities that match you.",
                        fontSize = 14.sp,
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isLoading && scholarshipList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else if (scholarshipList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No scholarships available.",
                            color = SecondaryText,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        items(scholarshipList) { scholarship ->
                            StudentScholarshipItem(
                                scholarship = scholarship,
                                onViewDetailsClick = {
                                    val intent = Intent(context, ScholarshipDetailsActivity::class.java).apply {
                                        putExtra("SCHOLARSHIP_ID", scholarship.id)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentScholarshipItem(
    scholarship: ScholarshipModel,
    onViewDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            // Scholarship Title
            Text(
                text = scholarship.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Amount (make it stand out)
            Text(
                text = "Amount: ${scholarship.amount}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Deadline
            Text(
                text = "Deadline: ${scholarship.deadline}",
                fontSize = 14.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = scholarship.description,
                fontSize = 14.sp,
                color = SecondaryText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onViewDetailsClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text("View Details")
                }
            }
        }
    }
}