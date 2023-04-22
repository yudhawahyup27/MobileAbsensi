package com.nairobi.absensi.dashboard.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.auth.Auth
import com.nairobi.absensi.models.User
import com.nairobi.absensi.models.UserModel
import com.nairobi.absensi.ui.components.EditTemplate
import com.nairobi.absensi.ui.components.EditTemplateMode

@Composable
fun EditAdmin(navController: NavController? = null, id: String) {
    val context = LocalContext.current
    val model = UserModel()
    val user = remember { mutableStateOf<User?>(null) }

    val loading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
        .setTitleText("Loading")
        .setContentText("Mengambil data admin")
    loading.setCancelable(false)

    if (user.value == null) {
        loading.show()
    }

    model.getUserById(id) {
        if (it != null) {
            loading.dismiss()
            user.value = it
        } else {
            loading.dismiss()
        }
    }

    if (user.value != null) {
        val showDelete = Auth.getUser()!!.id != user.value!!.id
        EditTemplate(navController, "Edit admin", user.value, mode = EditTemplateMode.EDIT, showDelete = showDelete)
    }
}