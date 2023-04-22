package com.nairobi.absensi.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nairobi.absensi.auth.Auth
import com.nairobi.absensi.auth.LoginActivity
import com.nairobi.absensi.dashboard.user.DashboardUserHome
import com.nairobi.absensi.dashboard.user.EditProifle
import com.nairobi.absensi.dashboard.user.Work
import com.nairobi.absensi.ui.theme.AbsensiTheme

class DashboardUserActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildContent()
    }

    override fun onStart() {
        super.onStart()
        checkState()
        Auth.listen {
            checkState()
        }
    }

    // Check user login
    private fun checkState() {
        if (!Auth.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Build content for dashboard user
    private fun buildContent() {
        setContent {
            // Theme
            AbsensiTheme {
                // Composable user dashboard
                DashboardUserCompose()
            }
        }
    }

    // Composable user dashboard
    @Composable
    fun DashboardUserCompose() {
        val navController = rememberNavController()
        // Navigation
        NavHost(navController = navController, startDestination = "home") {
            // User home
            composable("home") {
                DashboardUserHome(navController)
            }
            // User profile
            composable("profile") {
                EditProifle(navController)
            }
            // Work
            composable("work") {
                Work(navController)
            }
        }
    }
}