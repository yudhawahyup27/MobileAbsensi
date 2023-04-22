package com.nairobi.absensi.dashboard.user

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.nairobi.absensi.auth.Auth
import com.nairobi.absensi.ui.components.EditTemplate
import com.nairobi.absensi.ui.components.EditTemplateMode

@Composable
fun EditProifle(navController: NavController? = null) {
    EditTemplate(navController, "Edit profile", Auth.getUser()!!, EditTemplateMode.EDIT)
}