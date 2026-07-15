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
import com.example.scholarix.theme.Background
import com.example.scholarix.theme.PrimaryBlue
import com.example.scholarix.theme.ScholarixTheme
import com.example.scholarix.theme.SecondaryText
import com.example.scholarix.viewmodel.ScholarshipViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            scholarshipViewModel.getScholarshipsByProvider(uid)
        }

        setContent {
            ScholarixTheme {
                ProviderDashboardScreen(scholarshipViewModel)
            }
        }
    }
}

@Composable
fun ProviderDashboardScreen(viewModel: ScholarshipViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val scholarshipList by viewModel.scholarshipList.observeAsState<List<ScholarshipModel>>(initial = emptyList())
    val isLoading by viewModel.loading.observeAsState<Boolean>(initial = false)

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
                            viewModel.deleteScholarship(toDelete.id) { success, msg ->
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Dashboard Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome Provider",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Text(
                        text = "Manage your posted scholarships",
                        fontSize = 14.sp,
                        color = SecondaryText
                    )
                }

                // Simple logout text button
                TextButton(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        activity?.finish()
                    }
                ) {
                    Text("Logout", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
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

            Spacer(modifier = Modifier.height(20.dp))

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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = scholarship.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Amount: ${scholarship.amount}",
                    fontSize = 14.sp,
                    color = SecondaryText
                )
                Text(
                    text = "Deadline: ${scholarship.deadline}",
                    fontSize = 14.sp,
                    color = SecondaryText
                )
            }
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