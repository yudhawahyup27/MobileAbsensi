package com.nairobi.absensi.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.R
import com.nairobi.absensi.neuralnetwork.FaceDetector
import com.nairobi.absensi.types.Address
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.StorageModel
import com.nairobi.absensi.types.User
import com.nairobi.absensi.types.UserModel
import com.nairobi.absensi.types.UserRole
import com.nairobi.absensi.ui.theme.Aqua
import com.nairobi.absensi.utils.validateUser

// Mode for edit or add
enum class EditTemplateMode {
    ADD, EDIT
}

// Edit template
@Preview
@Composable
fun EditTemplate(
    navController: NavController? = null,
    title: String = "Edit",
    user: User? = null,
    mode: EditTemplateMode = EditTemplateMode.ADD,
    defaultRole: UserRole = UserRole.USER,
    showDelete: Boolean = false,
    isAdminDashboard: Boolean = false,
) {
    val context = LocalContext.current
    val storage = StorageModel()

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var nip by remember { mutableStateOf(TextFieldValue("")) }
    var dob by remember { mutableStateOf(Date()) }
    var address by remember { mutableStateOf(Address()) }
    var photo by remember { mutableStateOf("") }

    user?.let {
        email = TextFieldValue(it.email)
        password = TextFieldValue(it.password)
        name = TextFieldValue(it.name)
        phone = TextFieldValue(it.phone)
        nip = TextFieldValue(it.nip)
        dob = it.dob
        address = it.address
        storage.checkFile(it.id) { exist ->
            if (exist) {
                photo = it.id
            }
        }
    }

    val showPassword = {
        when (mode) {
            EditTemplateMode.EDIT -> {
                if (isAdminDashboard) {
                    user!!.role == UserRole.ADMIN
                } else {
                    true
                }
            }
            EditTemplateMode.ADD -> defaultRole == UserRole.ADMIN
        }
    }

    val getPassword = {
        when (mode) {
            EditTemplateMode.EDIT -> {
                if (isAdminDashboard && user!!.role == UserRole.USER) {
                    user!!.password
                } else {
                    password.text
                }
            }
            EditTemplateMode.ADD -> {
                if (defaultRole == UserRole.ADMIN) {
                    password.text
                } else {
                    "123456"
                }
            }
        }
    }

    // Column
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Simple appbar
        SimpleAppbar(
            navController = navController,
            title = title,
            modifier = Modifier
                .background(Aqua)
                .fillMaxWidth()
        )
        // Form
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Scrollable column
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Email field
                FormField(
                    value = email,
                    onValueChange = { email = it },
                    label = context.getString(R.string.email),
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                if (showPassword()) {
                    // Password field
                    FormField(
                        value = password,
                        onValueChange = { password = it },
                        label = context.getString(R.string.password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                // Name field
                FormField(
                    value = name,
                    onValueChange = { name = it },
                    label = context.getString(R.string.name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Phone field
                FormField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = context.getString(R.string.phone),
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // NIP field
                FormField(
                    value = nip,
                    onValueChange = { nip = it },
                    label = context.getString(R.string.nip),
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Date of birth field
                FormFieldDate(
                    value = dob,
                    onValueChange = { dob = it },
                    label = context.getString(R.string.date_of_birth),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Address field
                FormFieldLocation(
                    value = address,
                    onValueChange = { address = it },
                    label = context.getString(R.string.address),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Photo field
                FormFieldImage(
                    value = photo,
                    onValueChange = { photo = it },
                    label = context.getString(R.string.photo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Button
                Button(
                    onClick = {
                        val data: User
                        if (mode == EditTemplateMode.ADD) {
                            data = User()
                            data.role = defaultRole
                        } else {
                            data = user!!
                        }
                        data.email = email.text
                        data.password = getPassword()
                        data.name = name.text
                        data.phone = phone.text
                        data.nip = nip.text
                        data.dob = dob
                        data.address = address
                        saveUser(context, data, mode, photo) {
                            Auth.user?.let {
                                if (it.id == data.id) {
                                    Auth.user = data
                                }
                            }
                            navController?.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Aqua,
                        contentColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(context.getString(R.string.simpan))
                }
                // If edit mode, add delete button
                if (mode == EditTemplateMode.EDIT && showDelete) {
                    Button(
                        onClick = {
                            deleteUser(context, user!!) {
                                navController?.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(context.getString(R.string.hapus))
                    }
                }
            }
        }
    }
}

// Save user
private fun saveUser(
    context: Context,
    data: User,
    mode: EditTemplateMode,
    photo: String,
    callback: () -> Unit
) {
    val loading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
    loading.titleText = "${context.getString(R.string.saving)}..."
    loading.setCancelable(false)
    loading.show()

    val isUserValid = validateUser(context, data)
    if (!isUserValid.first) {
        loading.dismissWithAnimation()
        dialogError(
            context,
            context.getString(R.string.gagal),
            isUserValid.second
        )
        return
    }

    val model = UserModel()
    val storage = StorageModel()

    if (photo.isEmpty()) {
        loading.dismissWithAnimation()
        dialogError(
            context,
            context.getString(R.string.gagal),
            context.getString(R.string.empty_photo)
        )
    } else {
        if (mode == EditTemplateMode.ADD) {
            // Check if user already exist
            model.getUser(hashMapOf("email" to data.email)) {
                if (it != null) {
                    loading.dismissWithAnimation()
                    dialogError(
                        context,
                        context.getString(R.string.gagal),
                        context.getString(R.string.akun_sudah_ada)
                    )
                } else {
                    model.setUser(data) { success, id ->
                        if (!success) {
                            loading.dismissWithAnimation()
                            dialogError(
                                context,
                                context.getString(R.string.gagal),
                                context.getString(R.string.kesalahan_sistem)
                            )
                        } else {
                            FaceDetector().detectFromURI(context, photo) { success, face ->
                                if (!success) {
                                    loading.dismissWithAnimation()
                                    dialogError(
                                        context,
                                        context.getString(R.string.gagal),
                                        context.getString(R.string.face_not_found)
                                    )
                                } else {
                                    storage.uploadBitmap(
                                        id,
                                        face!!
                                    ) { uploaded ->
                                        if (!uploaded) {
                                            loading.dismissWithAnimation()
                                            dialogError(
                                                context,
                                                context.getString(R.string.gagal),
                                                context.getString(R.string.kesalahan_sistem)
                                            )
                                        } else {
                                            loading.dismissWithAnimation()
                                            dialogSuccess(
                                                context,
                                                context.getString(R.string.sukses),
                                                context.getString(R.string.user_dibuat)
                                            ) {
                                                callback()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (photo != data.id) {
                FaceDetector().detectFromURI(context, photo) { success, face ->
                    if (!success) {
                        loading.dismissWithAnimation()
                        dialogError(
                            context,
                            context.getString(R.string.gagal),
                            context.getString(R.string.face_not_found)
                        )
                    } else {
                        storage.uploadBitmap(data.id, face!!) {
                            model.updateUser(data) { success ->
                                loading.dismissWithAnimation()
                                if (!success) {
                                    dialogError(
                                        context,
                                        context.getString(R.string.gagal),
                                        context.getString(R.string.kesalahan_sistem)
                                    )
                                } else {
                                    dialogSuccess(
                                        context,
                                        context.getString(R.string.sukses),
                                        context.getString(R.string.user_diupdate)
                                    ) {
                                        callback()
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                model.updateUser(data) { success ->
                    loading.dismissWithAnimation()
                    if (!success) {
                        dialogError(
                            context,
                            context.getString(R.string.gagal),
                            context.getString(R.string.kesalahan_sistem)
                        )
                    } else {
                        dialogSuccess(
                            context,
                            context.getString(R.string.sukses),
                            context.getString(R.string.user_diupdate)
                        ) {
                            callback()
                        }
                    }
                }
            }
        }
    }
}

// Delete user
private fun deleteUser(context: Context, data: User, callback: () -> Unit) {
    val prompt = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
    prompt.titleText = context.getString(R.string.peringatan)
    prompt.contentText = context.getString(R.string.delete_user_prompt)
    prompt.confirmText = context.getString(R.string.ya)
    prompt.setCancelButton(context.getString(R.string.tidak)) {
        prompt.dismissWithAnimation()
    }
    prompt.setConfirmClickListener {
        prompt.dismissWithAnimation()
        UserModel().deleteUser(data) { success ->
            if (!success) {
                dialogError(
                    context,
                    context.getString(R.string.gagal),
                    context.getString(R.string.kesalahan_sistem)
                )
            } else {
                StorageModel().deleteFile(data.id) {}
                dialogSuccess(
                    context,
                    context.getString(R.string.sukses),
                    context.getString(R.string.user_dihapus)
                ) {
                    callback()
                }
            }
        }
    }
    prompt.show()
}