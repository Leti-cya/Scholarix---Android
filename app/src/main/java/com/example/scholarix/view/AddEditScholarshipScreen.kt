package com.example.scholarix.view

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    val fetchedScholarship by viewModel.scholarship.observeAsState<ScholarshipModel?>(initial = null)
    val isLoading by viewModel.loading.observeAsState<Boolean>(initial = false)

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
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Scholarship" else "Add Scholarship",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
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
                    ScholarshipInputField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Scholarship Title",
                        placeholder = "e.g. Merit-based Academic Scholarship"
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ScholarshipInputField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Description",
                        placeholder = "Describe the scholarship details...",
                        singleLine = false,
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ScholarshipInputField(
                        value = eligibility,
                        onValueChange = { eligibility = it },
                        label = "Eligibility",
                        placeholder = "e.g. CGPA > 3.5, Undergraduate students"
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ScholarshipInputField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = "Amount",
                        placeholder = "e.g. $5,000 / Semester"
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ScholarshipInputField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = "Deadline",
                        placeholder = "e.g. Dec 31, 2026"
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ScholarshipInputField(
                        value = applicationLink,
                        onValueChange = { applicationLink = it },
                        label = "Application Link",
                        placeholder = "e.g. https://example.com/apply"
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            if (title.isBlank() || description.isBlank() || eligibility.isBlank() ||
                                amount.isBlank() || deadline.isBlank() || applicationLink.isBlank()) {
                                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (uid == null) {
                                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                                return@Button
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
                                    // Keep providerId, createdAt, id
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
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Save",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun ScholarshipInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = singleLine,
        maxLines = maxLines,
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
            focusedIndicatorColor = PrimaryBlue.copy(alpha = 0.3f),
            focusedContainerColor = Color.Gray.copy(alpha = 0.05f)
        )
    )
}