package com.nairobi.absensi.dashboard.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.LeaveRequestModel
import com.nairobi.absensi.types.LeaveRequestStatus
import com.nairobi.absensi.ui.components.FormField
import com.nairobi.absensi.ui.components.FormFieldDate
import com.nairobi.absensi.ui.components.SimpleAppbar
import com.nairobi.absensi.ui.components.dialogError
import com.nairobi.absensi.ui.components.dialogSuccess
import com.nairobi.absensi.ui.theme.Purple

// Leave Request
@Composable
fun LeaveRequest(navController: NavController? = null) {
    val context = LocalContext.current

    val startTime = remember { mutableStateOf(Date()) }
    val endTime = remember { mutableStateOf(Date()) }
    val reason = remember { mutableStateOf(TextFieldValue("")) }

    // Column
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Simple Appbar
        SimpleAppbar(
            modifier = Modifier
                .fillMaxWidth(),
            background = Purple,
            navController = navController,
            title = context.getString(R.string.absen_cuti),
        )

        // Column
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(20.dp)
        ) {
            // Start Time
            FormFieldDate(
                value = startTime.value,
                onValueChange = { startTime.value = it },
                label = context.getString(R.string.dari_tanggal),
                modifier = Modifier.fillMaxWidth()
            )
            // End Time
            FormFieldDate(
                value = endTime.value,
                onValueChange = { endTime.value = it },
                label = context.getString(R.string.sampai_tanggal),
                modifier = Modifier.fillMaxWidth(),
            )
            // Reason
            FormField(
                value = reason.value,
                onValueChange = { reason.value = it },
                label = context.getString(R.string.alasan),
                modifier = Modifier.fillMaxWidth(),
            )
            // Submit
            Button(
                onClick = {
                    val loading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                    loading.titleText = context.getString(R.string.loading)
                    loading.setCancelable(false)
                    loading.show()

                    LeaveRequestModel().getLeaveRequestByUser(Auth.user!!.id) { requests ->
                        var allowed = true
                        var message = ""
                        requests.forEach { request ->
                            if (request.status == LeaveRequestStatus.PENDING) {
                                if (allowed) {
                                    allowed = false
                                    message = context.getString(R.string.request_pending_error)
                                }
                            }
                            if (request.end.before(startTime.value) || request.start.after(endTime.value)) {
                                if (allowed) {
                                    allowed = false
                                    message = context.getString(R.string.request_date_error)
                                }
                            }
                            if (request.start.before(Date()) || request.end.before(Date())) {
                                if (allowed) {
                                    allowed = false
                                    message = context.getString(R.string.request_past_error)
                                }
                            }
                        }
                        if (!allowed) {
                            loading.dismissWithAnimation()
                            SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(context.getString(R.string.gagal))
                                .setContentText(message)
                                .show()
                        } else {
                            val leaveRequest = com.nairobi.absensi.types.LeaveRequest()
                            leaveRequest.start = startTime.value
                            leaveRequest.end = endTime.value
                            leaveRequest.userId = Auth.user!!.id
                            LeaveRequestModel().setLeaveRequest(leaveRequest) {
                                loading.dismissWithAnimation()
                                if (it) {
                                    dialogSuccess(
                                        context,
                                        context.getString(R.string.sukses),
                                        context.getString(R.string.leave_request_success),
                                    ) {
                                        navController?.popBackStack()
                                    }
                                } else {
                                    dialogError(
                                        context,
                                        context.getString(R.string.gagal),
                                        context.getString(R.string.kesalahan_sistem),
                                    )
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text(context.getString(R.string.kirim), color = Color.White)
            }
        }
    }
}
