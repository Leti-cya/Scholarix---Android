package com.example.scholarix.ui.screens.provider

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scholarix.model.ScholarshipModel
import com.example.scholarix.repository.ScholarshipRepoImpl
import com.example.scholarix.ui.components.PrimaryButton
import com.example.scholarix.ui.components.TextField
import com.example.scholarix.ui.components.TopBar
import com.example.scholarix.ui.theme.Background
import com.example.scholarix.ui.theme.PrimaryBlue
import com.example.scholarix.ui.theme.ScholarixTheme
import com.example.scholarix.viewmodel.ScholarshipViewModel
import com.google.firebase.auth.FirebaseAuth

class AddEditScholarshipActivity : ComponentActivity() {
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

        val scholarshipId = intent.getStringExtra("SCHOLARSHIP_ID")
        if (!scholarshipId.isNullOrEmpty()) {
            scholarshipViewModel.getScholarship(scholarshipId)
        }

        setContent {
            ScholarixTheme {
                AddEditScholarshipScreen(scholarshipViewModel, scholarshipId)
            }
        }
    }
}

@Composable
fun AddEditScholarshipScreen(
    viewModel: ScholarshipViewModel,
    scholarshipId: String?
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val isEditMode = !scholarshipId.isNullOrEmpty()
    val fetchedScholarship by viewModel.scholarship.observeAsState(initial = null)
    val isLoading by viewModel.loading.observeAsState(initial = false)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var eligibility by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var applicationLink by remember { mutableStateOf("") }

    // Prefill form in edit mode once fetched
    LaunchedEffect(fetchedScholarship) {
        val scholarship = fetchedScholarship
        if (isEditMode && scholarship != null) {
            title = scholarship.title
            description = scholarship.description
            eligibility = scholarship.eligibility
            amount = scholarship.amount
            deadline = scholarship.deadline
            applicationLink = scholarship.applicationLink
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = if (isEditMode) "Edit Scholarship" else "Add Scholarship",
                showBackButton = true
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
        ) {
            if (isEditMode && fetchedScholarship == null && isLoading) {
                CircularProgressIndicator(
                    color = PrimaryBlue,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Scholarship Title",
                        placeholder = "e.g. Merit-based Academic Scholarship"
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Description",
                        placeholder = "Describe the scholarship details...",
                        singleLine = false
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = eligibility,
                        onValueChange = { eligibility = it },
                        label = "Eligibility",
                        placeholder = "e.g. CGPA > 3.5, Undergraduate students"
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = "Amount",
                        placeholder = "e.g. $5,000 / Semester"
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = "Deadline",
                        placeholder = "e.g. Dec 31, 2026"
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = applicationLink,
                        onValueChange = { applicationLink = it },
                        label = "Application Link",
                        placeholder = "e.g. https://example.com/apply"
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryButton(
                        text = if (isEditMode) "Save Changes" else "Publish Scholarship",
                        isLoading = isLoading,
                        onClick = {
                            if (title.isBlank() || description.isBlank() || eligibility.isBlank() ||
                                amount.isBlank() || deadline.isBlank() || applicationLink.isBlank()) {
                                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                                return@PrimaryButton
                            }

                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (uid == null) {
                                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                                return@PrimaryButton
                            }

                            val currentScholarship = fetchedScholarship
                            if (isEditMode && currentScholarship != null) {
                                val updatedScholarship = currentScholarship.copy(
                                    title = title.trim(),
                                    description = description.trim(),
                                    eligibility = eligibility.trim(),
                                    amount = amount.trim(),
                                    deadline = deadline.trim(),
                                    applicationLink = applicationLink.trim()
                                )
                                viewModel.updateScholarship(updatedScholarship.id, updatedScholarship) { success, msg ->
                                    if (success) {
                                        Toast.makeText(context, "Scholarship updated successfully!", Toast.LENGTH_SHORT).show()
                                        activity?.finish()
                                    } else {
                                        Toast.makeText(context, "Failed to update: $msg", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                val newScholarship = ScholarshipModel(
                                    providerId = uid,
                                    title = title.trim(),
                                    description = description.trim(),
                                    eligibility = eligibility.trim(),
                                    amount = amount.trim(),
                                    deadline = deadline.trim(),
                                    applicationLink = applicationLink.trim(),
                                    createdAt = System.currentTimeMillis()
                                )
                                viewModel.addScholarship(newScholarship) { success, msg ->
                                    if (success) {
                                        Toast.makeText(context, "Scholarship added successfully!", Toast.LENGTH_SHORT).show()
                                        activity?.finish()
                                    } else {
                                        Toast.makeText(context, "Failed to save: $msg", Toast.LENGTH_SHORT).show()
                                    }
                                }
                             }
                         }
                     )
                     Spacer(modifier = Modifier.height(40.dp))
                 }
             }
         }
     }
}