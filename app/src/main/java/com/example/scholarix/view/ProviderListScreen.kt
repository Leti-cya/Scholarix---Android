package com.example.scholarix.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scholarix.model.ProfileModel
import com.example.scholarix.model.UserModel
import com.example.scholarix.repo.ProfileRepoImpl
import com.example.scholarix.repo.UserRepoImpl
import com.example.scholarix.viewmodel.ProfileViewModel
import com.example.scholarix.viewmodel.UserViewModel
import com.example.scholarix.theme.Background
import com.example.scholarix.theme.DarkText
import com.example.scholarix.theme.PrimaryBlue
import com.example.scholarix.theme.ScholarixTheme
import com.example.scholarix.theme.SecondaryText

class ProviderListActivity : ComponentActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScholarixTheme {
                ProviderListScreen(userViewModel, profileViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        userViewModel.getAllUsers()
        profileViewModel.getAllProfiles()
    }
}

@Composable
fun ProviderListScreen(
    userViewModel: UserViewModel,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    
    val allUsers by userViewModel.allUsers.observeAsState(initial = null)
    val allProfiles by profileViewModel.allProfiles.observeAsState(initial = null)
    
    val isUserLoading by userViewModel.loading.observeAsState(initial = false)
    val isProfileLoading by profileViewModel.loading.observeAsState(initial = false)
    
    val showLoading = (allUsers == null || allProfiles == null) || isUserLoading || isProfileLoading

    Scaffold(
        bottomBar = {
            StudentBottomNavigation(selected = BottomNavItem.Providers)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Providers",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (showLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else {
                    val providers = allUsers?.filter { it?.role == "provider" } ?: emptyList()
                    
                    if (providers.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No providers available.",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DarkText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Check back later for registered scholarship providers.",
                                    fontSize = 14.sp,
                                    color = SecondaryText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(providers) { user ->
                                user?.let {
                                    val profile = allProfiles?.find { it.uid == user.id }
                                    ProviderCard(
                                        user = user,
                                        profile = profile,
                                        onViewProfileClick = {
                                            val intent = Intent(context, ProviderDetailsActivity::class.java).apply {
                                                putExtra("PROVIDER_ID", user.id)
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
    }
}

@Composable
fun ProviderCard(
    user: UserModel,
    profile: ProfileModel?,
    onViewProfileClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewProfileClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val orgName = profile?.name?.ifBlank { null } ?: user.fullName.ifBlank { "Unknown Provider" }
            val orgType = profile?.organizationType?.ifBlank { null } ?: "Provider"
            val description = profile?.description?.ifBlank { null } ?: "No description provided."

            Text(
                text = orgName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = orgType,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = SecondaryText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onViewProfileClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                ),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("View Profile")
            }
        }
    }
}