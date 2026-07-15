package com.example.scholarix.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

class ProviderDashboardActivity : ComponentActivity() {
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

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            scholarshipViewModel.getScholarshipsByProvider(uid)
            userViewModel.getUserById(uid)
        }

        setContent {
            ScholarixTheme {
                ProviderDashboardScreen(
                    scholarshipViewModel,
                    userViewModel
                )            }
        }
    }
}

@Composable
fun ProviderDashboardScreen(
    scholarshipViewModel: ScholarshipViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val scholarshipList by scholarshipViewModel.scholarshipList.observeAsState(initial = emptyList())
    val isLoading by scholarshipViewModel.loading.observeAsState(initial = false)

    val userModel by userViewModel.users.observeAsState(initial = null)

    val firstName =
        userModel?.fullName
            ?.trim()
            ?.split(" ")
            ?.firstOrNull()
            ?: ""

    var showDeleteDialog by remember { mutableStateOf(false) }
    var scholarshipToDelete by remember { mutableStateOf<ScholarshipModel?>(null) }

    if (showDeleteDialog && scholarshipToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                scholarshipToDelete = null
            },
            title = { Text(text = "Confirm Delete") },
            text = { Text(text = "Are you sure you want to delete this scholarship?") },
            confirmButton = {
                Button(
                    onClick = {
                        val toDelete = scholarshipToDelete
                        if (toDelete != null) {
                            scholarshipViewModel.deleteScholarship(toDelete.id) { success, msg ->
                                if (success) {
                                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error: $msg", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        showDeleteDialog = false
                        scholarshipToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteDialog = false
                        scholarshipToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            ProviderBottomNavigation(selected = BottomNavItem.Dashboard)
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
                        text = "Manage Scholarships",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Create and manage your scholarship opportunities.",
                        fontSize = 14.sp,
                        color = SecondaryText
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "${scholarshipList.size} Scholarship ${if (scholarshipList.size == 1) "" else "s"}Posted.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkText
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Add Scholarship Button
                Button(
                    onClick = {
                        val intent = Intent(context, AddEditScholarshipActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Scholarship",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Your Scholarships",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                            text = "No scholarships added yet.",
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
                            ScholarshipItem(
                                scholarship = scholarship,
                                onEditClick = {
                                    val intent = Intent(context, AddEditScholarshipActivity::class.java).apply {
                                        putExtra("SCHOLARSHIP_ID", scholarship.id)
                                    }
                                    context.startActivity(intent)
                                },
                                onDeleteClick = {
                                    scholarshipToDelete = scholarship
                                    showDeleteDialog = true
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
fun ScholarshipItem(
    scholarship: ScholarshipModel,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
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
            Text(
                text = scholarship.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Amount: ${scholarship.amount}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Deadline: ${scholarship.deadline}",
                fontSize = 14.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryBlue
                    ),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Edit")
                }
                Button(
                    onClick = onDeleteClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}