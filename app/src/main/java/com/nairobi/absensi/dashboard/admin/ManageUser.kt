package com.nairobi.absensi.dashboard.admin

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.nairobi.absensi.ui.components.ManageTemplate
import com.nairobi.absensi.utils.crashMe

// Manage user
@Preview
@Composable
fun ManageUser(navController: NavController? = null) {
    ManageTemplate(
        navController,
        "Kelola user",
        filter = { !it.isAdmin() },
        addRoute = "add_user",
        editRoute = "edit_user"
    )
}