package com.nairobi.absensi.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nairobi.absensi.LoginActivity
import com.nairobi.absensi.R
import com.nairobi.absensi.dashboard.user.DashboardUserHome
import com.nairobi.absensi.dashboard.user.EditProfile
import com.nairobi.absensi.dashboard.user.History
import com.nairobi.absensi.dashboard.user.Leave
import com.nairobi.absensi.dashboard.user.LeaveRequest
import com.nairobi.absensi.dashboard.user.OvertimeWork
import com.nairobi.absensi.dashboard.user.Work
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.ui.theme.AbsensiTheme

class DashboardUserActivity : ComponentActivity() {
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
        val context = LocalContext.current

        val navController = rememberNavController()
        // Navigation
        NavHost(
            navController = navController,
            startDestination = context.getString(R.string.home)
        ) {
            // User home
            composable(context.getString(R.string.home)) {
                DashboardUserHome(navController)
            }
            // User profile
            composable(context.getString(R.string.profile)) {
                EditProfile(navController)
            }
            // Work
            composable(context.getString(R.string.work)) {
                Work(navController)
            }
            // Leave
            composable(context.getString(R.string.leave)) {
                Leave(navController)
            }
            // Leave Request
            composable(context.getString(R.string.leave_request)) {
                LeaveRequest(navController)
            }
            // Overtime
            composable(context.getString(R.string.overtime)) {
                OvertimeWork(navController)
            }
            // History
            composable(context.getString(R.string.history)) {
                History(navController)
            }
        }
    }
}