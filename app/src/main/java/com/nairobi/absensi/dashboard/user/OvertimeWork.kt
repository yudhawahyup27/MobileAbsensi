package com.nairobi.absensi.dashboard.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.OvertimeModel
import com.nairobi.absensi.types.OvertimeStatus
import com.nairobi.absensi.types.Time
import com.nairobi.absensi.ui.components.dialogError
import com.nairobi.absensi.ui.components.dialogSuccess
import com.nairobi.absensi.ui.components.loadingDialog

// Overtime
@Composable
fun OvertimeWork(navController: NavController? = null) {
    val context = LocalContext.current
    val user = Auth.user!!

    LaunchedEffect("overtime") {
        val loading = loadingDialog(context)
        OvertimeModel().getOvertimeByUserId(user.id) {overtimes ->
            overtimes.filter { it.status == OvertimeStatus.PENDING }
                .forEach { overtime ->
                    if (!overtime.date.isToday() && overtime.date.before(Date())) {
                        overtime.status = OvertimeStatus.REJECTED
                        OvertimeModel().updateOvertime(overtime) {}
                    }
                }
        }
        OvertimeModel().getOvertimeByUserId(user.id) { overtimes ->
            val todo = overtimes.filter { it.status == OvertimeStatus.PENDING && it.date.isToday() }
            if (todo.isEmpty()) {
                loading.dismissWithAnimation()
                dialogError(
                    context,
                    context.getString(R.string.gagal),
                    context.getString(R.string.overtime_date_error)
                ) {
                    navController?.popBackStack()
                }
            } else {
                val overtime = todo.first()
                if (overtime.end.before(Time())) {
                    overtime.status = OvertimeStatus.REJECTED
                    OvertimeModel().updateOvertime(overtime) {
                        loading.dismissWithAnimation()
                        dialogError(
                            context,
                            context.getString(R.string.gagal),
                            context.getString(R.string.overtime_date_error)
                        ) {
                            navController?.popBackStack()
                        }
                    }
                } else if (overtime.start.after(Time())) {
                    loading.dismissWithAnimation()
                    dialogError(
                        context,
                        context.getString(R.string.gagal),
                        context.getString(R.string.work_time_error_before)
                    ) {
                        navController?.popBackStack()
                    }
                } else {
                    isNearOffice(context, navController!!) {
                        overtime.status = OvertimeStatus.APPROVED
                        OvertimeModel().updateOvertime(overtime) {
                            loading.dismissWithAnimation()
                            dialogSuccess(
                                context,
                                context.getString(R.string.sukses),
                                context.getString(R.string.absen_sukses)
                            ) {
                                navController?.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}