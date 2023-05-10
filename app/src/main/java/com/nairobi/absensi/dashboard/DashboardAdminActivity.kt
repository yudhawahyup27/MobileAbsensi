package com.nairobi.absensi.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nairobi.absensi.LoginActivity
import com.nairobi.absensi.R
import com.nairobi.absensi.dashboard.admin.AddOvertime
import com.nairobi.absensi.dashboard.admin.AddUser
import com.nairobi.absensi.dashboard.admin.DashboardAdminHome
import com.nairobi.absensi.dashboard.admin.EditUser
import com.nairobi.absensi.dashboard.admin.ExportData
import com.nairobi.absensi.dashboard.admin.ManageAbsence
import com.nairobi.absensi.dashboard.admin.ManageLeave
import com.nairobi.absensi.dashboard.admin.ManageOffice
import com.nairobi.absensi.dashboard.admin.ManageOvertime
import com.nairobi.absensi.dashboard.admin.ManageUser
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.types.UserRole
import com.nairobi.absensi.ui.theme.AbsensiTheme

class DashboardAdminActivity : ComponentActivity() {
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
        val context = LocalContext.current
        val navController = rememberNavController()
        // Navigation
        NavHost(
            navController = navController,
            startDestination = context.getString(R.string.home)
        ) {
            // Admin home
            composable(context.getString(R.string.home)) {
                DashboardAdminHome(navController)
            }
            // Manage admin
            composable(
                "${context.getString(R.string.manage_user)}/{${context.getString(R.string.role)}}",
                arguments = listOf(
                    navArgument(context.getString(R.string.role)) { type = NavType.StringType }
                )
            ) {
                val role = UserRole.valueOf(it.arguments?.getString(context.getString(R.string.role)) ?: "")
                ManageUser(navController, role)
            }
            // Manage absence
            composable(context.getString(R.string.manage_absence)) {
                ManageAbsence(navController)
            }
            // Manage leave
            composable(context.getString(R.string.manage_leave)) {
                ManageLeave(navController)
            }
            // Manage overtime
            composable(context.getString(R.string.manage_overtime)) {
                ManageOvertime(navController)
            }
            // Add overtime
            composable(context.getString(R.string.add_overtime)) {
                AddOvertime(navController)
            }
            // Manage office
            composable(context.getString(R.string.manage_office)) {
                ManageOffice(navController)
            }
            // Export data
            composable(context.getString(R.string.export_data)) {
                ExportData(navController)
            }
            // Add user
            composable(
                "${context.getString(R.string.add_user)}/{${context.getString(R.string.role)}}",
                arguments = listOf(
                    navArgument(context.getString(R.string.role)) { type = NavType.StringType }
                )
            ) {
                val role = UserRole.valueOf(it.arguments?.getString(context.getString(R.string.role)) ?: "")
                AddUser(navController, role)
            }
            // Edit
            composable(
                "${context.getString(R.string.edit_user)}/{${context.getString(R.string.role)}}/{${context.getString(R.string.id)}}",
                arguments = listOf(
                    navArgument(context.getString(R.string.role)) { type = NavType.StringType },
                    navArgument(context.getString(R.string.id)) { type = NavType.StringType }
                )
            ) {
                val role = UserRole.valueOf(it.arguments?.getString(context.getString(R.string.role)) ?: "")
                val id = it.arguments?.getString(context.getString(R.string.id)) ?: ""
                EditUser(navController, role, id)
            }
        }
    }
}