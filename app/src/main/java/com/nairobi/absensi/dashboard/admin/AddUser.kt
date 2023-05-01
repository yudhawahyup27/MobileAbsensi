package com.nairobi.absensi.dashboard.admin

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.nairobi.absensi.R
import com.nairobi.absensi.types.UserRole
import com.nairobi.absensi.ui.components.EditTemplate
import com.nairobi.absensi.ui.components.EditTemplateMode

// Add User
@Composable
fun AddUser(navController: NavController? = null, role: UserRole) {
    val context = LocalContext.current
    val text =
        if (role == UserRole.ADMIN) context.getString(R.string.admin) else context.getString(R.string.user)

    // Edit Template
    EditTemplate(
        navController,
        "${context.getString(R.string.tambah)} $text",
        mode = EditTemplateMode.ADD,
        defaultRole = role
    )
}