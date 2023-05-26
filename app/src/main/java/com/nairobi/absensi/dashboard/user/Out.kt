package com.nairobi.absensi.dashboard.user

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Absence
import com.nairobi.absensi.types.AbsenceModel
import com.nairobi.absensi.types.AbsenceType
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.OfficeModel
import com.nairobi.absensi.types.Time
import com.nairobi.absensi.types.User
import com.nairobi.absensi.ui.components.dialogError
import com.nairobi.absensi.ui.components.dialogSuccess
import com.nairobi.absensi.ui.components.loadingDialog

// Out
@Composable
fun Out(navController: NavController? = null) {
    val context = LocalContext.current
    val user = Auth.user!!

    LaunchedEffect("out") {
        val controller = navController!!
        val dialog = loadingDialog(context)
        isNearOffice(context, navController) {

            isHoliday(user, context, navController) {
                AbsenceModel().getAbsenceByUserId(user.id) { absence ->

                    if (absence.isEmpty()) {
                        dialog.dismissWithAnimation()
                        dialogSuccess(
                            context,
                            context.getString(R.string.sukses),
                            context.getString(R.string.absen_berhasil)
                        ) {
                            controller.popBackStack()
                        }
                    } else {
                        absence.filter { it.type == AbsenceType.ONWORK }
                        if (absence.isEmpty()) {
                            dialog.dismissWithAnimation()
                            dialogSuccess(
                                context,
                                context.getString(R.string.sukses),
                                context.getString(R.string.absen_berhasil)
                            ) {
                                controller.popBackStack()
                            }
                        } else {
                            val absences = ArrayList<Absence>()
                            absence.forEach {
                                it.type = AbsenceType.WORK
                                if (it.date.isToday()) {
                                    it.endDate = Date()
                                }
                                absences.add(it)
                            }
                            AbsenceModel().updateAbsences(absences) {
                                dialog.dismissWithAnimation()
                                dialogSuccess(
                                    context,
                                    context.getString(R.string.sukses),
                                    context.getString(R.string.absen_berhasil)
                                ) {
                                    controller.popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }



    fun isoutTime(user: User, context: Context, navController: NavController, callback: () -> Unit) {
        val loading = loadingDialog(context)
        val now = Time()
        OfficeModel().getOffice { data ->
            if (now.before(data.endTime)) {
                loading.dismissWithAnimation()
                dialogError(
                    context,
                    context.getString(R.string.gagal),
                    context.getString(R.string.out_time_error_before)
                ) {
                    navController.popBackStack()
                }
            } else {
                loading.dismissWithAnimation()
                callback()
            }
        }
    }

