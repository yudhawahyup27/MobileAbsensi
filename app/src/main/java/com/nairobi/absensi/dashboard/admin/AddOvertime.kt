package com.nairobi.absensi.dashboard.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.Overtime
import com.nairobi.absensi.types.OvertimeModel
import com.nairobi.absensi.types.OvertimeStatus
import com.nairobi.absensi.types.Time
import com.nairobi.absensi.types.UserModel
import com.nairobi.absensi.ui.components.FormField
import com.nairobi.absensi.ui.components.FormFieldDate
import com.nairobi.absensi.ui.components.FormFieldTime
import com.nairobi.absensi.ui.components.SimpleAppbar
import com.nairobi.absensi.ui.components.dialogError
import com.nairobi.absensi.ui.components.dialogSuccess
import com.nairobi.absensi.ui.components.loadingDialog
import com.nairobi.absensi.ui.theme.Purple

// Add overtime
@Composable
fun AddOvertime(navController: NavController? = null) {
    val context = LocalContext.current

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var date by remember { mutableStateOf(Date()) }
    var startTime by remember { mutableStateOf(Time()) }
    var endTime by remember { mutableStateOf(Time()) }

    // Column
    Column(
        Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        // Simple appbar
        SimpleAppbar(
            navController = navController,
            title = context.getString(R.string.overtime),
            background = Purple,
            modifier = Modifier
                .fillMaxWidth()
        )
        // Column
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Email
            FormField(
                value = email,
                onValueChange = { email = it },
                label = context.getString(R.string.email),
                modifier = Modifier
                    .fillMaxWidth()
            )
            // Date
            FormFieldDate(
                value = date,
                onValueChange = { date = it },
                label = context.getString(R.string.tanggal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )
            // Start time
            FormFieldTime(
                value = startTime,
                onValueChange = { startTime = it },
                label = context.getString(R.string.jam_mulai),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )
            // End time
            FormFieldTime(
                value = endTime,
                onValueChange = { endTime = it },
                label = context.getString(R.string.jam_selesai),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )
            // Submit button
            Button(
                onClick = {
                    val loading = loadingDialog(context)
                    loading.show()
                    if (email.text.isEmpty()) {
                        loading.dismissWithAnimation()
                        dialogError(
                            context,
                            context.getString(R.string.gagal),
                            context.getString(R.string.empty_email)
                        )
                    } else {
                        UserModel().getUser(
                            hashMapOf(
                                "email" to email.text,
                            )
                        ) {
                            if (it == null) {
                                loading.dismissWithAnimation()
                                dialogError(
                                    context,
                                    context.getString(R.string.gagal),
                                    context.getString(R.string.user_not_found, email.text)
                                )
                            } else {
                                val overtimeData = Overtime()
                                overtimeData.status = OvertimeStatus.PENDING
                                overtimeData.date = date
                                overtimeData.start = startTime
                                overtimeData.end = endTime
                                overtimeData.userId = it.id
                                OvertimeModel().addOvertime(overtimeData) {status ->
                                    loading.dismissWithAnimation()
                                    if (status) {
                                        dialogSuccess(
                                            context,
                                            context.getString(R.string.sukses),
                                            context.getString(R.string.sukses)
                                        ) {
                                            navController?.popBackStack()
                                        }
                                    } else {
                                        dialogError(
                                            context,
                                            context.getString(R.string.gagal),
                                            context.getString(R.string.kesalahan_sistem)
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                    contentColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text(context.getString(R.string.simpan))
            }
        }
    }
}