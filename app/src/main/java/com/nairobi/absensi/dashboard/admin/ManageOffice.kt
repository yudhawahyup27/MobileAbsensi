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
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.models.Address
import com.nairobi.absensi.models.OfficeData
import com.nairobi.absensi.models.OfficeModel
import com.nairobi.absensi.models.TimeData
import com.nairobi.absensi.ui.components.FormFieldLocation
import com.nairobi.absensi.ui.components.FormFieldTime
import com.nairobi.absensi.ui.components.SimpleAppbar
import com.nairobi.absensi.ui.theme.Purple

@Composable
fun ManageOffice(navController: NavController? = null) {
    val context = LocalContext.current
    var loaded by remember { mutableStateOf(false) }
    val model = OfficeModel()
    var address by remember { mutableStateOf(TextFieldValue("")) }
    var startTime by remember { mutableStateOf(TextFieldValue("")) }
    var endTime by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        SimpleAppbar(
            title = "Kelola Kantor",
            navController = navController,
            background = Purple,
            modifier = Modifier
                .fillMaxWidth()
        )
        Column(
            Modifier
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Address
            FormFieldLocation(
                value = address,
                onValueChange = { address = it },
                label = "Alamat Kantor",
                modifier = Modifier
                    .fillMaxWidth()
            )
            // Start Time
            FormFieldTime(
                value = startTime,
                onValueChange = { startTime = it },
                label = "Jam Mulai",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            // End Time
            FormFieldTime(
                value = endTime,
                onValueChange = { endTime = it },
                label = "Jam Selesai",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            // Save Button
            Button(
                onClick = {
                    val data = OfficeData.empty()
                    data.address = Address.fromAddressString(context, address.text)
                    data.startTime = TimeData.fromString(startTime.text)
                    data.endTime = TimeData.fromString(endTime.text)

                    val loading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                    loading.setTitleText("Loading...")
                    loading.setCancelable(false)
                    loading.show()
                    model.setOfficeData(data) {
                        loading.dismiss()
                        if (it) {
                            SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Berhasil")
                                .setContentText("Data kantor berhasil disimpan")
                                .setConfirmText("OK")
                                .show()
                        } else {
                            SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Gagal")
                                .setContentText("Data kantor gagal disimpan")
                                .setConfirmText("OK")
                                .show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
            ) {
                Text(text = "Simpan")
            }
        }
    }

    if (!loaded) {
        loaded = true
        model.getOfficeData {
            address = TextFieldValue(it.address.toAddressString(context))
            startTime = TextFieldValue(it.startTime.string())
            endTime = TextFieldValue(it.endTime.string())
        }
    }
}