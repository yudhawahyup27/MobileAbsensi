package com.nairobi.absensi.dashboard.admin

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.nairobi.absensi.ui.components.ManageTemplate

// Manage admin
@Preview
@Composable
fun ManageAdmin(navController: NavController? = null) {
    ManageTemplate(
        navController,
        "Kelola admin",
        filter = { it.isAdmin() },
        addRoute = "add_admin",
        editRoute = "edit_admin"
    )
}