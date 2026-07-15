package com.example.scholarix.view

import android.app.Activity
import android.os.Bundle
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
import com.example.scholarix.model.UserModel
import com.example.scholarix.repo.ScholarshipRepoImpl
import com.example.scholarix.repo.UserRepoImpl
import com.example.scholarix.theme.Background
import com.example.scholarix.theme.PrimaryBlue
import com.example.scholarix.theme.ScholarixTheme
import com.example.scholarix.theme.SecondaryText
import com.example.scholarix.viewmodel.ScholarshipViewModel
import com.example.scholarix.viewmodel.UserViewModel

class ScholarshipDetailsActivity : ComponentActivity() {
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

        val scholarshipId = intent.getStringExtra("SCHOLARSHIP_ID") ?: ""
        if (scholarshipId.isNotEmpty()) {
            scholarshipViewModel.getScholarship(scholarshipId)
        }

        setContent {
            ScholarixTheme {
                ScholarshipDetailsScreen(scholarshipViewModel, userViewModel, scholarshipId)
            }
        }
    }
}

@Composable
fun ScholarshipDetailsScreen(
    scholarshipViewModel: ScholarshipViewModel,
    userViewModel: UserViewModel,
    scholarshipId: String
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val scholarship by scholarshipViewModel.scholarship.observeAsState(initial = null)
    val isScholarshipLoading by scholarshipViewModel.loading.observeAsState(initial = false)
    val providerUser by userViewModel.users.observeAsState(initial = null)
    val isUserLoading by userViewModel.loading.observeAsState(initial = false)

    var showContactDialog by remember { mutableStateOf(false) }

    // Fetch provider details once scholarship is loaded
    LaunchedEffect(scholarship?.providerId) {
        val providerId = scholarship?.providerId
        if (!providerId.isNullOrEmpty()) {
            userViewModel.getUserById(providerId)
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text(
                        text = "Scholarship Details",
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
        },
        bottomBar = {
            if (scholarship != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Button(
                        onClick = { showContactDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Apply",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
        ) {
            if (isScholarshipLoading && scholarship == null) {
                CircularProgressIndicator(
                    color = PrimaryBlue,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (scholarship == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Scholarship details could not be found.",
                        color = SecondaryText,
                        fontSize = 16.sp
                    )
                }
            } else {
                val currentScholarship = scholarship!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        text = currentScholarship.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Highlights Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Amount",
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                                Text(
                                    text = currentScholarship.amount,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Deadline",
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                                Text(
                                    text = currentScholarship.deadline,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Details Section
                    DetailSection(title = "Description", content = currentScholarship.description)
                    Spacer(modifier = Modifier.height(20.dp))

                    DetailSection(title = "Eligibility Requirements", content = currentScholarship.eligibility)
                    Spacer(modifier = Modifier.height(20.dp))

                    DetailSection(title = "Application Link", content = currentScholarship.applicationLink)
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = {
                Text(
                    text = "Provider Contact",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Please contact the scholarship provider using the information below to continue your application.",
                        fontSize = 14.sp,
                        color = SecondaryText,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    if (isUserLoading && providerUser == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(24.dp))
                        }
                    } else {
                        val provider = providerUser
                        val providerName = provider?.fullName ?: "Provider"
                        val providerEmail = provider?.email ?: "Not Available"
                        val providerPhone = provider?.contact ?: "Not Available"

                        Text(
                            text = "Name: $providerName",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Email: $providerEmail",
                            fontSize = 15.sp,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Phone: $providerPhone",
                            fontSize = 15.sp,
                            color = PrimaryBlue
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showContactDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun DetailSection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 15.sp,
            color = SecondaryText,
            lineHeight = 22.sp
        )
    }
}
