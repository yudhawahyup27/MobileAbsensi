package com.nairobi.absensi.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nairobi.absensi.auth.Auth
import com.nairobi.absensi.auth.LoginActivity
import com.nairobi.absensi.dashboard.admin.AddAdmin
import com.nairobi.absensi.dashboard.admin.AddUser
import com.nairobi.absensi.dashboard.admin.DashboardAdminHome
import com.nairobi.absensi.dashboard.admin.EditAdmin
import com.nairobi.absensi.dashboard.admin.EditUser
import com.nairobi.absensi.dashboard.admin.ManageAbsence
import com.nairobi.absensi.dashboard.admin.ManageAdmin
import com.nairobi.absensi.dashboard.admin.ManageLeave
import com.nairobi.absensi.dashboard.admin.ManageOffice
import com.nairobi.absensi.dashboard.admin.ManageOvertime
import com.nairobi.absensi.dashboard.admin.ManageUser
import com.nairobi.absensi.ui.theme.AbsensiTheme

class DashboardAdminActivity: ComponentActivity() {
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

    // Build content for dashboard admin
    private fun buildContent() {
        setContent {
            // Theme
            AbsensiTheme {
                // Composable admin dashboard
                DashboardAdminCompose()
            }
        }
    }

    // Composable admin dashboard
    @Composable
    fun DashboardAdminCompose() {
        val navController = rememberNavController()
        // Navigation
        NavHost(navController = navController, startDestination = "home") {
            // Admin home
            composable("home") {
                DashboardAdminHome(navController)
            }
            // Manage admin
            composable("manage_admin") {
                ManageAdmin(navController)
            }
            // Manage user
            composable("manage_user") {
                ManageUser(navController)
            }
            // Manage absence
            composable("manage_absence") {
                ManageAbsence(navController)
            }
            // Manage leave
            composable("manage_leave") {
                ManageLeave(navController)
            }
            // Manage overtime
            composable("manage_overtime") {
                ManageOvertime(navController)
            }
            // Manage office
            composable("manage_office") {
                ManageOffice(navController)
            }
            // Add admin
            composable("add_admin") {
                AddAdmin(navController)
            }
            // Edit admin
            composable(
                "edit_admin/{user}",
                arguments = listOf(navArgument("user") { type = NavType.StringType })
            ) {
                EditAdmin(navController, it.arguments?.getString("user") ?: "")
            }
            // Add user
            composable("add_user") {
                AddUser(navController)
            }
            // Edit user
            composable(
                "edit_user/{user}",
                arguments = listOf(navArgument("user") { type = NavType.StringType })
            ) {
                EditUser(navController, it.arguments?.getString("user") ?: "")
            }
        }
    }
}