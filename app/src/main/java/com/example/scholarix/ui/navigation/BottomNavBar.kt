package com.example.scholarix.ui.navigation

import android.app.Activity
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.scholarix.ui.screens.provider.ProviderDashboardActivity
import com.example.scholarix.ui.screens.provider.ProviderProfileActivity
import com.example.scholarix.ui.screens.student.ProviderListActivity
import com.example.scholarix.ui.screens.student.StudentDashboardActivity
import com.example.scholarix.ui.screens.student.StudentProfileActivity
import com.example.scholarix.ui.theme.PrimaryBlue
import com.example.scholarix.ui.theme.SecondaryText

enum class BottomNavItem {
    Home,
    Providers,
    Profile,
    Dashboard
}

@Composable
fun StudentBottomNavigation(selected: BottomNavItem) {
    val context = LocalContext.current
    val activity = context as? Activity

    NavigationBar(
        containerColor = Color.White
    ) {
        // Home
        NavigationBarItem(
            selected = selected == BottomNavItem.Home,
            onClick = {
                if (selected != BottomNavItem.Home) {
                    val intent = Intent(context, StudentDashboardActivity::class.java)
                    context.startActivity(intent)
                    activity?.finish()
                    activity?.overridePendingTransition(0, 0)
                }
            },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = SecondaryText,
                unselectedTextColor = SecondaryText,
                indicatorColor = PrimaryBlue
            )
        )

        // Providers
        NavigationBarItem(
            selected = selected == BottomNavItem.Providers,
            onClick = {
                if (selected != BottomNavItem.Providers) {
                    val intent = Intent(context, ProviderListActivity::class.java)
                    context.startActivity(intent)
                    activity?.finish()
                    activity?.overridePendingTransition(0, 0)
                }
            },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "Providers") },
            label = { Text("Providers") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = SecondaryText,
                unselectedTextColor = SecondaryText,
                indicatorColor = PrimaryBlue
            )
        )

        // Profile
        NavigationBarItem(
            selected = selected == BottomNavItem.Profile,
            onClick = {
                if (selected != BottomNavItem.Profile) {
                    val intent = Intent(context, StudentProfileActivity::class.java)
                    context.startActivity(intent)
                    activity?.finish()
                    activity?.overridePendingTransition(0, 0)
                }
            },
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = SecondaryText,
                unselectedTextColor = SecondaryText,
                indicatorColor = PrimaryBlue
            )
        )
    }
}

@Composable
fun ProviderBottomNavigation(selected: BottomNavItem) {
    val context = LocalContext.current
    val activity = context as? Activity

    NavigationBar(
        containerColor = Color.White
    ) {
        // Scholarships
        NavigationBarItem(
            selected = selected == BottomNavItem.Dashboard,
            onClick = {
                if (selected != BottomNavItem.Dashboard) {
                    val intent = Intent(context, ProviderDashboardActivity::class.java)
                    context.startActivity(intent)
                    activity?.finish()
                    activity?.overridePendingTransition(0, 0)
                }
            },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = SecondaryText,
                unselectedTextColor = SecondaryText,
                indicatorColor = PrimaryBlue
            )
        )

        // Profile
        NavigationBarItem(
            selected = selected == BottomNavItem.Profile,
            onClick = {
                if (selected != BottomNavItem.Profile) {
                    val intent = Intent(context, ProviderProfileActivity::class.java)
                    context.startActivity(intent)
                    activity?.finish()
                    activity?.overridePendingTransition(0, 0)
                }
            },
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = SecondaryText,
                unselectedTextColor = SecondaryText,
                indicatorColor = PrimaryBlue
            )
        )
    }
}