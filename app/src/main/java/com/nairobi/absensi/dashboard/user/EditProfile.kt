package com.nairobi.absensi.dashboard.user

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.ui.components.EditTemplate
import com.nairobi.absensi.ui.components.EditTemplateMode

// Edit Profile
@Composable
fun EditProfile(navController: NavController? = null) {
    EditTemplate(navController, "Edit profile", Auth.user!!, EditTemplateMode.EDIT)
}