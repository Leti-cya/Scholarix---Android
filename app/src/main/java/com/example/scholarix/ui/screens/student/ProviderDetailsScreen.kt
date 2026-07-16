package com.example.scholarix.ui.screens.student

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scholarix.model.ScholarshipModel
import com.example.scholarix.repository.ProfileRepoImpl
import com.example.scholarix.repository.ScholarshipRepoImpl
import com.example.scholarix.repository.UserRepoImpl
import com.example.scholarix.ui.theme.Background
import com.example.scholarix.ui.theme.DarkText
import com.example.scholarix.ui.theme.PrimaryBlue
import com.example.scholarix.ui.theme.ScholarixTheme
import com.example.scholarix.ui.theme.SecondaryText
import com.example.scholarix.viewmodel.ProfileViewModel
import com.example.scholarix.viewmodel.ScholarshipViewModel
import com.example.scholarix.viewmodel.UserViewModel

class ProviderDetailsActivity : ComponentActivity() {
    private var providerId: String = ""

    private val userViewModel: UserViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(UserRepoImpl()) as T
            }
        }
    }

    private val profileViewModel: ProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(ProfileRepoImpl()) as T
            }
        }
    }

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

        providerId = intent.getStringExtra("PROVIDER_ID") ?: ""

        setContent {
            ScholarixTheme {
                ProviderDetailsScreen(
                    providerId = providerId,
                    userViewModel = userViewModel,
                    profileViewModel = profileViewModel,
                    scholarshipViewModel = scholarshipViewModel
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (providerId.isNotEmpty()) {
            userViewModel.getUserById(providerId)
            profileViewModel.getProfile(providerId)
            scholarshipViewModel.getScholarshipsByProvider(providerId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDetailsScreen(
    providerId: String,
    userViewModel: UserViewModel,
    profileViewModel: ProfileViewModel,
    scholarshipViewModel: ScholarshipViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val userModel by userViewModel.users.observeAsState(initial = null)
    val profileModel by profileViewModel.profile.observeAsState(initial = null)
    val scholarshipsList by scholarshipViewModel.scholarshipList.observeAsState(initial = null)

    val isUserLoading by userViewModel.loading.observeAsState(initial = false)
    val isProfileLoading by profileViewModel.loading.observeAsState(initial = false)
    val isScholarshipLoading by scholarshipViewModel.loading.observeAsState(initial = false)

    val showLoading = (userModel == null && isUserLoading) || 
                      (profileModel == null && isProfileLoading) || 
                      (scholarshipsList == null && isScholarshipLoading)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Provider Details",
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
            if (showLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (userModel == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Provider details could not be loaded.",
                        color = SecondaryText,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val orgName = profileModel?.name?.ifBlank { null } ?: userModel?.fullName?.ifBlank { "Unknown Provider" } ?: "Unknown Provider"
                val orgType = profileModel?.organizationType?.ifBlank { null } ?: "Scholarship Provider"
                val description = profileModel?.description?.ifBlank { null } ?: "No description provided."
                val address = profileModel?.address?.ifBlank { null } ?: "Not provided"
                val website = profileModel?.website?.ifBlank { null } ?: "Not provided"
                val firstLetter = orgName.trim().firstOrNull()?.toString()?.uppercase() ?: "?"

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(PrimaryBlue, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = firstLetter,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Organization name & type
                    Text(
                        text = orgName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = orgType,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryBlue,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Details Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Provider Profile",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            DetailItem(
                                icon = Icons.Default.LocationOn,
                                label = "Address",
                                value = address
                            )
                            HorizontalDivider(color = Background.copy(alpha = 0.5f), thickness = 1.dp)

                            DetailItem(
                                icon = Icons.Default.Search,
                                label = "Website",
                                value = website
                            )
                            HorizontalDivider(color = Background.copy(alpha = 0.5f), thickness = 1.dp)

                            DetailItem(
                                icon = Icons.Default.Info,
                                label = "Description",
                                value = description
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Posted Scholarships Title
                    Text(
                        text = "Available Scholarships",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    val scholarships = scholarshipsList ?: emptyList()
                    if (scholarships.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No scholarships available.",
                                    color = SecondaryText,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        scholarships.forEach { scholarship ->
                            DetailScholarshipCard(
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
                    
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = SecondaryText,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                color = DarkText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DetailScholarshipCard(
    scholarship: ScholarshipModel,
    onViewDetailsClick: () -> Unit
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
            Spacer(modifier = Modifier.height(6.dp))
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
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onViewDetailsClick,
                    shape = RoundedCornerShape(8.dp),
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