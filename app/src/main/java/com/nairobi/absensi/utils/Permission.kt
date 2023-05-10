package com.nairobi.absensi.utils

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog

// Required permissions
val permissions = arrayOf(
    android.Manifest.permission.INTERNET,
    android.Manifest.permission.ACCESS_NETWORK_STATE,
    android.Manifest.permission.ACCESS_COARSE_LOCATION,
    android.Manifest.permission.ACCESS_FINE_LOCATION,
    android.Manifest.permission.CAMERA,
    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    android.Manifest.permission.READ_EXTERNAL_STORAGE
)

// Dialog alert
fun showPermissionDialogAlert(context: Context) {
    val dialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
    dialog.titleText = "Permission Required"
    dialog.contentText = "Please allow all permission to continue"
    dialog.confirmText = "Grant"
    dialog.setConfirmClickListener {
        requestPermission(context)
        it.dismissWithAnimation()
    }
    dialog.show()
}

// Check permission
fun checkPermission(context: Context): Boolean {
    // Check permission
    val isGranted = permissions.all {
        context.checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
    if (!isGranted) {
        // Request permission
        requestPermission(context)
        // re-check permission
        return permissions.all {
            context.checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }
    return true
}

// Request permission
fun requestPermission(context: Context) {
    // Request permission
    (context as android.app.Activity).requestPermissions(permissions, 0)
}