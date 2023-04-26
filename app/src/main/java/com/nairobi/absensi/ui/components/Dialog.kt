package com.nairobi.absensi.ui.components;

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.R

// Error dialog
fun dialogError(context: Context, title: String, message: String, callback: () -> Unit = {}) {
    SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
        .setTitleText(title)
        .setContentText(message)
        .setConfirmClickListener {
            it.dismissWithAnimation()
            callback()
        }
        .setConfirmText(context.getString(R.string.ok))
        .show()
}

// Success dialog
fun dialogSuccess(context: Context, title: String, message: String, callback: () -> Unit = {}) {
    SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
        .setTitleText(title)
        .setContentText(message)
        .setConfirmClickListener {
            it.dismissWithAnimation()
            callback()
        }
        .setConfirmText(context.getString(R.string.ok))
        .show()
}

// Loading dialog
fun loadingDialog(context: Context): SweetAlertDialog {
    val dialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
    dialog.titleText = context.getString(R.string.loading)
    dialog.setCancelable(false)
    dialog.show()
    return dialog
}