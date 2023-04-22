package com.nairobi.absensi.dashboard.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.api.getHolidayData
import com.nairobi.absensi.auth.Auth
import com.nairobi.absensi.models.AbsenceData
import com.nairobi.absensi.models.AbsenceModel
import java.util.Date

// Work
@Composable
fun Work(navController: NavController? = null) {
    val context = LocalContext.current
    val user = Auth.getUser()!!
    val model = AbsenceModel()

    // Check if user has already checked in
    model.getAbsenceDataByUserId(user.id) { lists1 ->
        val filtered = lists1.filter { it.date.isToday() }
        if (filtered.isNotEmpty()) {
            // User has already checked in
            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Kamu sudah absen hari ini")
                .setContentText("Kamu sudah absen hari ini, kamu tidak bisa absen lagi")
                .setConfirmText("OK")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    navController?.popBackStack()
                }
                .show()

            return@getAbsenceDataByUserId
        } else {
            // Check if today is sunday
            val date = Date()
            if (date.day == 0) {
                // Today is sunday
                SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Hari ini hari minggu")
                    .setContentText("Hari ini hari minggu, kamu tidak bisa absen")
                    .setConfirmText("OK")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        navController?.popBackStack()
                    }
                    .show()

                return@getAbsenceDataByUserId
            } else {
                // Check if today is holiday
                getHolidayData { lists2 ->
                    val filtered2 = lists2.filter { it.isToday() }
                    if (filtered2.isNotEmpty()) {
                        // Today is holiday
                        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Hari ini hari libur")
                            .setContentText("Hari ini hari libur, kamu tidak bisa absen")
                            .setConfirmText("OK")
                            .setConfirmClickListener { sDialog ->
                                sDialog.dismissWithAnimation()
                                navController?.popBackStack()
                            }
                            .show()
                        return@getHolidayData
                    } else {
                        navController?.popBackStack()
                        return@getHolidayData
                    }
                }
            }
        }
    }
}