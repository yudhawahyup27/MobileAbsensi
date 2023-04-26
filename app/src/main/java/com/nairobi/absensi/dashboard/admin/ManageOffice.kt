package com.nairobi.absensi.dashboard.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Address
import com.nairobi.absensi.types.Office
import com.nairobi.absensi.types.OfficeModel
import com.nairobi.absensi.types.Time
import com.nairobi.absensi.ui.components.FormFieldLocation
import com.nairobi.absensi.ui.components.FormFieldTime
import com.nairobi.absensi.ui.components.SimpleAppbar
import com.nairobi.absensi.ui.components.dialogError
import com.nairobi.absensi.ui.components.dialogSuccess
import com.nairobi.absensi.ui.theme.Purple

// Manage Office
@Composable
fun ManageOffice(navController: NavController? = null) {
    val context = LocalContext.current
    var loaded by remember { mutableStateOf(false) }
    val model = OfficeModel()
    var address by remember { mutableStateOf(Address()) }
    var startTime by remember { mutableStateOf(Time()) }
    var endTime by remember { mutableStateOf(Time()) }

    LaunchedEffect(context.getString(R.string.manage_office)) {
        model.getOffice {
            address = it.address
            startTime = it.startTime
            endTime = it.endTime
        }
    }

    // Column
    Column(
        Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        // Simple Appbar
        SimpleAppbar(
            title = context.getString(R.string.kelola_kantor),
            navController = navController,
            background = Purple,
            modifier = Modifier
                .fillMaxWidth()
        )
        // Content
        Column(
            Modifier
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Address
            FormFieldLocation(
                value = address,
                onValueChange = { address = it },
                label = context.getString(R.string.alamat_kantor),
                modifier = Modifier
                    .fillMaxWidth()
            )
            // Start Time
            FormFieldTime(
                value = startTime,
                onValueChange = { startTime = it },
                label = context.getString(R.string.jam_mulai),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            // End Time
            FormFieldTime(
                value = endTime,
                onValueChange = { endTime = it },
                label = context.getString(R.string.jam_selesai),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            // Save Button
            Button(
                onClick = {
                    val data = Office()
                    data.address = address
                    data.startTime = startTime
                    data.endTime = endTime

                    val loading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                    loading.setTitleText(context.getString(R.string.loading))
                    loading.setCancelable(false)
                    loading.show()
                    model.updateOffice(data) {
                        loading.dismiss()
                        if (it) {
                            dialogSuccess(
                                context,
                                context.getString(R.string.sukses),
                                context.getString(R.string.office_updated)
                            )
                        } else {
                            dialogError(
                                context,
                                context.getString(R.string.gagal),
                                context.getString(R.string.kesalahan_sistem)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
            ) {
                Text(context.getString(R.string.simpan))
            }
        }
    }
}