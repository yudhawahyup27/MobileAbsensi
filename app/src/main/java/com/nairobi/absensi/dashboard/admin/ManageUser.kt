package com.nairobi.absensi.dashboard.admin

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.nairobi.absensi.R
import com.nairobi.absensi.types.UserRole
import com.nairobi.absensi.ui.components.ManageTemplate

// Manage user
@Composable
fun ManageUser(navController: NavController? = null, role: UserRole) {
    val context = LocalContext.current
    val text = if  (role == UserRole.ADMIN) context.getString(R.string.admin) else context.getString(R.string.user)
    ManageTemplate(
        navController,
        "${context.getString(R.string.kelola)} $text",
        filter = { if (role == UserRole.ADMIN) it.isAdmin else !it.isAdmin },
        addRoute = "${context.getString(R.string.add_user)}/${role.name}",
        editRoute = "${context.getString(R.string.edit_user)}/${role.name}"
    )
}