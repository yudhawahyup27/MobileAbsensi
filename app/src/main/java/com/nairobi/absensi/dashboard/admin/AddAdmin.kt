package com.nairobi.absensi.dashboard.admin

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.nairobi.absensi.models.UserRole
import com.nairobi.absensi.ui.components.EditTemplate
import com.nairobi.absensi.ui.components.EditTemplateMode

@Composable
fun AddAdmin(navController: NavController? = null) {
    EditTemplate(navController, "Tambah admin", mode = EditTemplateMode.ADD, defaultRole = UserRole.ADMIN)
}