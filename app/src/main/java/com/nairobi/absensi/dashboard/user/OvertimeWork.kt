package com.nairobi.absensi.dashboard.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Auth
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
        OvertimeModel().getOvertimeByUserId(user.id) { overtimes ->
            val today = overtimes.filter { it.date.isToday() }
            if (today.isEmpty()) {
                loading.dismissWithAnimation()
                dialogError(
                    context,
                    context.getString(R.string.gagal),
                    context.getString(R.string.overtime_date_error)
                )
                navController?.popBackStack()
            } else {
                val overtime = today.find { it.status != OvertimeStatus.PENDING }
                val time = Time()
                if (overtime == null || time.before(overtime.start) || time.after(overtime.end)) {
                    if (time.after(overtime!!.end)) {
                        overtime.status = OvertimeStatus.REJECTED
                        OvertimeModel().updateOvertime(overtime) {
                        }
                    }
                    loading.dismissWithAnimation()
                    dialogError(
                        context,
                        context.getString(R.string.gagal),
                        context.getString(R.string.overtime_date_error),
                    )
                    navController?.popBackStack()
                } else {
                    loading.dismissWithAnimation()
                    isNearOffice(user, context, navController!!) {
                        overtime.status = OvertimeStatus.APPROVED
                        OvertimeModel().updateOvertime(overtime) {
                            dialogSuccess(
                                context,
                                context.getString(R.string.sukses),
                                context.getString(R.string.absen_sukses)
                            )
                        }
                    }
                }
            }
        }
    }
}