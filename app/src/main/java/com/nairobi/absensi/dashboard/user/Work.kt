package com.nairobi.absensi.dashboard.user

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.nairobi.absensi.R
import com.nairobi.absensi.api.getHolidayData
import com.nairobi.absensi.neuralnet.FaceRecognition
import com.nairobi.absensi.types.Absence
import com.nairobi.absensi.types.AbsenceModel
import com.nairobi.absensi.types.AbsenceType
import com.nairobi.absensi.types.Address
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.LeaveRequestModel
import com.nairobi.absensi.types.LeaveRequestStatus
import com.nairobi.absensi.types.OfficeModel
import com.nairobi.absensi.types.StorageModel
import com.nairobi.absensi.types.Time
import com.nairobi.absensi.types.User
import com.nairobi.absensi.ui.components.dialogError
import com.nairobi.absensi.ui.components.dialogSuccess
import com.nairobi.absensi.ui.components.loadingDialog
import com.schaefer.livenesscamerax.domain.model.CameraLens
import com.schaefer.livenesscamerax.domain.model.StepLiveness
import com.schaefer.livenesscamerax.presentation.model.CameraSettings
import com.schaefer.livenesscamerax.presentation.navigation.LivenessEntryPoint

// Work
@Composable
fun Work(navController: NavController? = null) {
    val context = LocalContext.current
    val user = Auth.user!!

    LaunchedEffect("first") {
        pendingLeaveRequest(user, context, navController!!) {
            alreadyAbsence(user, context, navController) {
                isHoliday(user, context, navController) {
                    isWorkTime(user, context, navController) {
                        isNearOffice(user, context, navController) {
                            verifyFace(user, context, navController) {
                                dialogSuccess(
                                    context,
                                    context.getString(R.string.sukses),
                                    "Terimakasih sudah melakukan absen"
                                ) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Verify face
fun verifyFace(user: User, context: Context, navController: NavController, callback: () -> Unit) {
    val loading = loadingDialog(context)
    LivenessEntryPoint.startLiveness(
        cameraSettings = CameraSettings(
            cameraLens = CameraLens.DEFAULT_FRONT_CAMERA,
            livenessStepList = arrayListOf(
                StepLiveness.STEP_LUMINOSITY,
                StepLiveness.STEP_BLINK,
                StepLiveness.STEP_SMILE,
            )
        ),
        context = context,
    ) { result ->
        result.createdBySteps?.let { steps ->
            loading.dismissWithAnimation()
            callback()
        }
    }
}

// Check if user location is near office
fun isNearOffice(user: User, context: Context, navController: NavController, callback: () -> Unit) {
    val loading = loadingDialog(context)
    val prov = LocationServices.getFusedLocationProviderClient(context)
    if (context.checkSelfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        loading.dismissWithAnimation()
        dialogError(
            context,
            context.getString(R.string.gagal),
            context.getString(R.string.location_permission_error)
        )
        return
    }
    prov.lastLocation.addOnSuccessListener { loc ->
        val userAddress = Address(loc)
        OfficeModel().getOffice { data ->
            if (!userAddress.near(data.address, 500)) {
                loading.dismissWithAnimation()
                dialogError(
                    context,
                    context.getString(R.string.gagal),
                    context.getString(R.string.office_distance_error)
                )
                navController.popBackStack()
            } else {
                loading.dismissWithAnimation()
                callback()
            }
        }
    }
    prov.lastLocation.addOnFailureListener {
        dialogError(
            context,
            context.getString(R.string.gagal),
            context.getString(R.string.location_error)
        )
    }
}

// Check if now is holiday
fun isHoliday(user: User, context: Context, navController: NavController, callback: () -> Unit) {
    val loading = loadingDialog(context)
    getHolidayData { holidays ->
        val today = holidays.find { it.isToday() }
        if (today != null || Date().isSunday()) {
            val absence = Absence()
            absence.date = Date()
            absence.userId = user.id
            absence.type = AbsenceType.HOLIDAY
            AbsenceModel().addAbsence(absence) {
                loading.dismissWithAnimation()
                dialogError(
                    context,
                    context.getString(R.string.gagal),
                    context.getString(R.string.holiday_error)
                )
                navController.popBackStack()
            }
        } else {
            loading.dismissWithAnimation()
            callback()
        }
    }
}

// Check if now is work time
fun isWorkTime(user: User, context: Context, navController: NavController, callback: () -> Unit) {
    val loading = loadingDialog(context)
    val now = Time()
    OfficeModel().getOffice { data ->
        if (now.before(data.startTime)) {
            loading.dismissWithAnimation()
            dialogError(
                context,
                context.getString(R.string.gagal),
                context.getString(R.string.work_time_error_before)
            )
            navController.popBackStack()
        } else if (now.after(data.endTime)) {
            val absence = Absence()
            absence.date = Date()
            absence.userId = user.id
            absence.type = AbsenceType.UNKNOWN
            AbsenceModel().addAbsence(absence) {
                loading.dismissWithAnimation()
                dialogError(
                    context,
                    context.getString(R.string.gagal),
                    context.getString(R.string.work_time_error_after)
                )
                navController.popBackStack()
            }
        } else {
            loading.dismissWithAnimation()
            callback()
        }
    }
}

// Check if there is pending leave request
fun pendingLeaveRequest(
    user: User,
    context: Context,
    navController: NavController,
    callback: () -> Unit
) {
    val loading = loadingDialog(context)
    LeaveRequestModel().getLeaveRequestByUser(user.id) { data ->
        loading.dismissWithAnimation()
        val pending = data.filter { it.status == LeaveRequestStatus.PENDING }
        if (pending.isNotEmpty()) {
            dialogError(
                context,
                context.getString(R.string.gagal),
                context.getString(R.string.request_pending_error)
            )
            navController.popBackStack()
        } else {
            callback()
        }
    }
}

// Check if user already absen today
fun alreadyAbsence(
    user: User,
    context: Context,
    navController: NavController,
    callback: () -> Unit
) {
    val loading = loadingDialog(context)
    AbsenceModel().getAbsenceByUserId(user.id) { data ->
        val today = data.filter { it.date.isToday() }
        loading.dismissWithAnimation()
        if (today.isNotEmpty()) {
            dialogError(
                context,
                context.getString(R.string.gagal),
                context.getString(R.string.already_absence_error)
            )
            navController.popBackStack()
        } else {
            callback()
        }
    }
}