package com.nairobi.absensi.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.auth.Auth
import com.nairobi.absensi.models.Address
import com.nairobi.absensi.models.StorageModel
import com.nairobi.absensi.models.User
import com.nairobi.absensi.models.UserModel
import com.nairobi.absensi.models.UserRole
import com.nairobi.absensi.ui.theme.Aqua
import java.util.Date

// Manage template
@SuppressLint("MutableCollectionMutableState")
@Composable
fun ManageTemplate(
    navController: NavController? = null,
    title: String = "",
    filter: (User) -> Boolean = { true },
    addRoute: String = "",
    editRoute: String = ""
) {
    val context = LocalContext.current
    var searchValue by remember { mutableStateOf(TextFieldValue()) }
    val model = UserModel()
    var lists by remember { mutableStateOf<ArrayList<User>>(arrayListOf()) }
    // Constraint layout
    ConstraintLayout(
        Modifier
            .fillMaxSize()
    ) {
        val (main, fab) = createRefs()
        // Column
        Column(
            Modifier
                .background(Aqua)
                .fillMaxSize()
                .padding(16.dp)
                .constrainAs(main) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            // Simple appbar
            SimpleAppbar(
                navController,
                title = title,
                modifier = Modifier
                    .fillMaxWidth()
            )
            // Search field
            FormFieldSearch(
                value = searchValue, onValueChange = { searchValue = it }, modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
            // Card
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Show filtered data
                lists.filter {
                    val address = it.address.toAddressString(context)
                    it.name.contains(searchValue.text, true)
                            || it.email.contains(searchValue.text, true)
                            || address.contains(searchValue.text, true)
                }.forEach {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                        ),
                        modifier = Modifier
                            .clickable {
                                navController?.navigate("$editRoute/${it.id}")
                            }
                            .fillMaxWidth()
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(it.name)
                            Text(it.email)
                        }
                    }
                }
            }
        }
        // Floating action button
        FloatingActionButton(
            onClick = {
                navController?.navigate(addRoute)
            },
            shape = MaterialTheme.shapes.large.copy(
                CornerSize(percent = 50)
            ),
            containerColor = Color.White,
            contentColor = Aqua,
            modifier = Modifier
                .padding(bottom = 20.dp, end = 20.dp)
                .constrainAs(fab) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }

    // fetch data
    model.getUsers(filter = filter) {
        lists.clear()
        lists = it
    }
}

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
) {
    val context = LocalContext.current
    val model = UserModel()
    val storage = StorageModel()

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var nip by remember { mutableStateOf(TextFieldValue("")) }
    var dob by remember { mutableStateOf(TextFieldValue("")) }
    var address by remember { mutableStateOf(TextFieldValue("")) }
    var photo by remember { mutableStateOf("") }

    user?.let {
        email = TextFieldValue(it.email)
        password = TextFieldValue(it.password)
        name = TextFieldValue(it.name)
        phone = TextFieldValue(it.phone)
        nip = TextFieldValue(it.nip)
        dob = TextFieldValue(it.dob.toString())
        address = TextFieldValue(it.address.toAddressString(context))
        storage.checkFile(it.id) { exist ->
            if (exist) {
                photo = it.id
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
                    label = "Email",
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Password field
                FormField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Name field
                FormField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Name",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Phone field
                FormField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone",
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // NIP field
                FormField(
                    value = nip,
                    onValueChange = { nip = it },
                    label = "NIP",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Date of birth field
                FormFieldDate(
                    value = dob,
                    onValueChange = { dob = it },
                    label = "Date of birth",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Address field
                FormFieldLocation(
                    value = address,
                    onValueChange = { address = it },
                    label = "Address",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Photo field
                FormFieldImage(
                    value = photo,
                    onValueChange = { photo = it },
                    label = "Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Button
                Button(
                    onClick = {
                        val data: User
                        if (mode == EditTemplateMode.ADD) {
                            data = User.empty()
                            data.role = defaultRole
                        } else {
                            data = user!!
                        }
                        data.email = email.text
                        data.password = password.text
                        data.name = name.text
                        data.phone = phone.text
                        data.nip = nip.text
                        data.dob = Date(dob.text)
                        data.address = Address.fromAddressString(context, address.text)

                        val loading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                            .setTitleText("Saving...")
                        loading.setCancelable(false)
                        loading.show()

                        if (photo.isEmpty()) {
                            loading.dismissWithAnimation()
                            SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Gagal!")
                                .setContentText("Foto tidak boleh kosong!")
                                .setConfirmText("OK")
                                .setConfirmClickListener { sDialog ->
                                    sDialog.dismissWithAnimation()
                                }
                                .show()
                        } else {
                            if (mode == EditTemplateMode.ADD) {
                                data.isValid { b, s ->
                                    if (!b) {
                                        loading.dismissWithAnimation()
                                        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Gagal!")
                                            .setContentText(s)
                                            .setConfirmText("OK")
                                            .setConfirmClickListener { sDialog ->
                                                sDialog.dismissWithAnimation()
                                            }
                                            .show()
                                    } else {
                                        model.createUser(data) { s, id ->
                                            if (s) {
                                                storage.uploadWithUri(context, id, photo) {
                                                    loading.dismissWithAnimation()
                                                    SweetAlertDialog(
                                                        context,
                                                        SweetAlertDialog.SUCCESS_TYPE
                                                    )
                                                        .setTitleText("Berhasil!")
                                                        .setContentText("Data berhasil ditambahkan!")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener { sDialog ->
                                                            sDialog.dismissWithAnimation()
                                                            navController?.popBackStack()
                                                        }
                                                        .show()
                                                }
                                            } else {
                                                loading.dismissWithAnimation()
                                                SweetAlertDialog(
                                                    context,
                                                    SweetAlertDialog.ERROR_TYPE
                                                )
                                                    .setTitleText("Gagal!")
                                                    .setContentText("Data gagal ditambahkan!")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener { sDialog ->
                                                        sDialog.dismissWithAnimation()
                                                    }
                                                    .show()
                                            }
                                        }
                                    }
                                }
                            } else {
                                data.isValid { b, s ->
                                    if (!b) {
                                        loading.dismissWithAnimation()
                                        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Gagal!")
                                            .setContentText(s)
                                            .setConfirmText("OK")
                                            .setConfirmClickListener { sDialog ->
                                                sDialog.dismissWithAnimation()
                                            }
                                            .show()
                                    } else {
                                        model.updateUser(data) {
                                            if (Auth.getUser()!!.id == data.id) {
                                                Auth.updateUser(data)
                                            }
                                            if (photo != data.id) {
                                                storage.uploadWithUri(context, data.id, photo) {
                                                    loading.dismissWithAnimation()
                                                    SweetAlertDialog(
                                                        context,
                                                        SweetAlertDialog.SUCCESS_TYPE
                                                    )
                                                        .setTitleText("Berhasil!")
                                                        .setContentText("Data berhasil diperbarui!")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener { sDialog ->
                                                            sDialog.dismissWithAnimation()
                                                            navController?.navigateUp()
                                                        }
                                                        .show()
                                                }
                                            } else {
                                                loading.dismissWithAnimation()
                                                SweetAlertDialog(
                                                    context,
                                                    SweetAlertDialog.SUCCESS_TYPE
                                                )
                                                    .setTitleText("Berhasil!")
                                                    .setContentText("Data berhasil diperbarui!")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener { sDialog ->
                                                        sDialog.dismissWithAnimation()
                                                        navController?.navigateUp()
                                                    }
                                                    .show()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Aqua,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Save")
                }
                // If edit mode, add delete button
                if (mode == EditTemplateMode.EDIT && showDelete) {
                    Button(
                        onClick = {
                            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Apakah anda yakin?")
                                .setContentText("Data yang dihapus tidak dapat dikembalikan!")
                                .setConfirmText("Ya, hapus!")
                                .setConfirmClickListener { sDialog ->
                                    sDialog.dismissWithAnimation()
                                    user?.let { data ->
                                        model.deleteUser(data) {
                                            if (it) {
                                                storage.deleteFile(data.id) {
                                                    SweetAlertDialog(
                                                        context,
                                                        SweetAlertDialog.SUCCESS_TYPE
                                                    )
                                                        .setTitleText("Berhasil!")
                                                        .setContentText("Data berhasil dihapus!")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener { sDialog ->
                                                            sDialog.dismissWithAnimation()
                                                            navController?.popBackStack()
                                                        }
                                                        .show()
                                                }
                                            } else {
                                                SweetAlertDialog(
                                                    context,
                                                    SweetAlertDialog.ERROR_TYPE
                                                )
                                                    .setTitleText("Gagal!")
                                                    .setContentText("Data gagal dihapus!")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener { sDialog ->
                                                        sDialog.dismissWithAnimation()
                                                    }
                                                    .show()
                                            }
                                        }
                                    }
                                }
                                .setCancelButton("Batal", SweetAlertDialog::dismissWithAnimation)
                                .show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }
}