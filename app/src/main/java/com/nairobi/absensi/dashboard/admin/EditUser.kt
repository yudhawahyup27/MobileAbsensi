package com.nairobi.absensi.dashboard.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.types.User
import com.nairobi.absensi.types.UserModel
import com.nairobi.absensi.types.UserRole
import com.nairobi.absensi.ui.components.EditTemplate
import com.nairobi.absensi.ui.components.EditTemplateMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Edit User
@Composable
fun EditUser(navController: NavController? = null, role: UserRole, id: String) {
    val context = LocalContext.current
    val model = viewModel<EditViewModel>()
    val text =
        if (role == UserRole.ADMIN) context.getString(R.string.admin) else context.getString(R.string.user)

    val loading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
    loading.titleText = context.getString(R.string.loading)
    loading.contentText = context.getString(R.string.mengambil_data)
    loading.setCancelable(false)

    when (val state = model.state.collectAsState().value) {
        EditViewModel.State.Loading -> {
            loading.show()
            model.load(id) {
                loading.dismiss()
            }
        }

        is EditViewModel.State.Success -> {
            // Edit Template
            EditTemplate(
                navController,
                "${context.getString(R.string.edit)} $text",
                state.user,
                mode = EditTemplateMode.EDIT,
                showDelete = id != Auth.user!!.id,
                isAdminDashboard = true,
            )
        }
    }
}

// ViewModel
class EditViewModel : ViewModel() {
    sealed class State {
        object Loading : State()
        data class Success(val user: User) : State()
    }

    private var _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    fun load(id: String, callback: () -> Unit) {
        UserModel().getUserById(id) {
            it?.let { user ->
                callback()
                _state.value = State.Success(user)
            }
        }
    }
}