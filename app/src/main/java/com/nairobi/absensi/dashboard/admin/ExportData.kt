package com.nairobi.absensi.dashboard.admin

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nairobi.absensi.R
import com.nairobi.absensi.types.AbsenceModel
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.LeaveRequestModel
import com.nairobi.absensi.types.OvertimeModel
import com.nairobi.absensi.types.User
import com.nairobi.absensi.types.UserModel
import com.nairobi.absensi.ui.components.FormFieldDate
import com.nairobi.absensi.ui.components.SimpleAppbar
import com.nairobi.absensi.ui.theme.Purple
import org.apache.poi.xssf.usermodel.XSSFWorkbook

// CardButton
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardButton(
    onClick: () -> Unit = {},
    icon: ImageVector,
    title: String,
    selected: Boolean = false,
    modifier: Modifier
) {
    val containerColor = if (selected) Purple else Color.White
    val contentColor = if (selected) Color.White else Color.Black
    Card(
        onClick = onClick, colors = CardDefaults.cardColors(
            containerColor = containerColor, contentColor = contentColor
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp, pressedElevation = 4.dp, disabledElevation = 0.dp
        ), modifier = modifier
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Icon
            Icon(
                icon, contentDescription = null, modifier = Modifier.padding(end = 16.dp)
            )
            // Text
            Text(
                text = title, fontSize = 16.sp
            )
        }
    }
}

enum class ExportDataType {
    ABSENCE, LEAVE, OVERTIME
}

// Export data
@Composable
fun ExportData(navController: NavController?) {
    val context = LocalContext.current
    val exportDataType = remember { mutableStateOf(ExportDataType.ABSENCE) }
    val startDate = remember { mutableStateOf(Date()) }
    val endDate = remember { mutableStateOf(Date()) }

    val getTitle = {
        when (exportDataType.value) {
            ExportDataType.ABSENCE -> context.getString(R.string.absensi)
            ExportDataType.LEAVE -> context.getString(R.string.cuti)
            ExportDataType.OVERTIME -> context.getString(R.string.lembur)
        }
    }

    val book = remember { mutableStateOf(XSSFWorkbook()) }

    val callback: (Boolean) -> Unit = {
        if (it) {
            Toast.makeText(
                context,
                context.getString(R.string.berhasil_diunduh),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "Something went wrong",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri ->
        if (uri != null) {
            val outputStream = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                book.value.write(outputStream)
                outputStream.close()
                callback(true)
            } else {
                callback(false)
            }
        } else {
            callback(false)
        }
    }

    // Column
    Column(
        Modifier.fillMaxSize()
    ) {
        // Simple appbar
        SimpleAppbar(
            title = context.getString(R.string.exportdata),
            navController = navController,
            background = Purple,
            modifier = Modifier.fillMaxWidth()
        )
        // Column
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Absence
            CardButton(
                onClick = { exportDataType.value = ExportDataType.ABSENCE },
                icon = Icons.Default.DateRange,
                title = context.getString(R.string.absensi),
                selected = exportDataType.value == ExportDataType.ABSENCE,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // Leave
            CardButton(
                onClick = { exportDataType.value = ExportDataType.LEAVE },
                icon = Icons.Default.AddTask,
                title = context.getString(R.string.cuti),
                selected = exportDataType.value == ExportDataType.LEAVE,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // Overtime
            CardButton(
                onClick = { exportDataType.value = ExportDataType.OVERTIME },
                icon = Icons.Default.Bedtime,
                title = context.getString(R.string.lembur),
                selected = exportDataType.value == ExportDataType.OVERTIME,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // Title
            Text(
                text = "${context.getString(R.string.export)} ${getTitle()}",
                fontSize = 16.sp,
                color = Purple,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // Start date
            FormFieldDate(
                label = context.getString(R.string.dari_tanggal),
                value = startDate.value,
                onValueChange = { startDate.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // End date
            FormFieldDate(
                label = context.getString(R.string.sampai_tanggal),
                value = endDate.value,
                onValueChange = { endDate.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // Button
            Button(
                onClick = {
                    when (exportDataType.value) {
                        ExportDataType.ABSENCE -> getUsers { users ->
                            getAbsenceExcel(
                                context, startDate.value, endDate.value, users
                            ) { xss ->
                                book.value = xss
                                launcher.launch(
                                    "${getTitle()}.xlsx"
                                )
                            }
                        }

                        ExportDataType.LEAVE -> getUsers { users ->
                            getLeaveExcel(
                                context, startDate.value, endDate.value, users
                            ) { xss ->
                                book.value = xss
                                launcher.launch(
                                    "${getTitle()}.xlsx"
                                )
                            }
                        }

                        ExportDataType.OVERTIME -> getUsers { users ->
                            getOvertimeExcel(
                                context, startDate.value, endDate.value, users
                            ) { xss ->
                                book.value = xss
                                launcher.launch(
                                    "${getTitle()}.xlsx"
                                )
                            }
                        }
                    }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Purple, contentColor = Color.White
                ), modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = context.getString(R.string.export), fontSize = 16.sp
                )
            }
        }
    }
}

// get Users
fun getUsers(callback: (HashMap<String, User>) -> Unit) {
    UserModel().getUsers({ true }) { users ->
        val hashMap = HashMap<String, User>()
        users.forEach { user ->
            hashMap[user.id] = user
        }
        callback(hashMap)
    }
}

// get absence Excel
fun getAbsenceExcel(
    context: Context,
    start: Date,
    end: Date,
    users: HashMap<String, User>,
    callback: (XSSFWorkbook) -> Unit
) {
    AbsenceModel().getAbsences { absences ->
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Absensi")
        val headerRow = sheet.createRow(0)
        val headerCell = headerRow.createCell(0)
        headerCell.setCellValue("Absensi")
        val row = sheet.createRow(1)
        val cell = row.createCell(0)
        cell.setCellValue("Nama")
        val cell2 = row.createCell(1)
        cell2.setCellValue("Tanggal")
        val cell3 = row.createCell(2)
        cell3.setCellValue("Jam Masuk")
        val cell4 = row.createCell(3)
        cell4.setCellValue("Jam Keluar")
        val cell5 = row.createCell(4)
        cell5.setCellValue("Keterangan")
        var i = 2
        for (absence in absences) {
            val user = users[absence.userId]!!
            val row = sheet.createRow(i)
            val cell = row.createCell(0)
            cell.setCellValue(user.name)
            val cell2 = row.createCell(1)
            cell2.setCellValue(absence.date.string())
            val cell3 = row.createCell(2)
            cell3.setCellValue(absence.date.time.string())
            val cell4 = row.createCell(3)
            cell4.setCellValue(absence.endDate?.time?.string() ?: "-")
            val cell5 = row.createCell(4)
            cell5.setCellValue(absence.type.name)
            i++
        }
        callback(workbook)
    }
}

// get leave Excel
fun getLeaveExcel(
    context: Context,
    start: Date,
    end: Date,
    users: HashMap<String, User>,
    callback: (XSSFWorkbook) -> Unit
) {
    LeaveRequestModel().getLeaveRequests { leaves ->
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Cuti")
        val headerRow = sheet.createRow(0)
        val headerCell = headerRow.createCell(0)
        headerCell.setCellValue("Cuti")
        val row = sheet.createRow(1)
        val cell = row.createCell(0)
        cell.setCellValue("Nama")
        val cell2 = row.createCell(1)
        cell2.setCellValue("Mulai")
        val cell3 = row.createCell(2)
        cell3.setCellValue("Sampai")
        val cell4 = row.createCell(3)
        cell4.setCellValue("Durasi")
        val cell5 = row.createCell(4)
        cell5.setCellValue("Keterangan")
        var i = 2
        for (leave in leaves) {
            val user = users[leave.userId]!!
            val row = sheet.createRow(i)
            val cell = row.createCell(0)
            cell.setCellValue(user.name)
            val cell2 = row.createCell(1)
            cell2.setCellValue(leave.start.string())
            val cell3 = row.createCell(2)
            cell3.setCellValue(leave.end.string())
            val cell4 = row.createCell(3)
            cell4.setCellValue("${leave.start.daysBetween(leave.end)} hari")
            val cell5 = row.createCell(4)
            cell5.setCellValue(leave.reason)
            i++
        }
        callback(workbook)
    }
}

// get overtime Excel
fun getOvertimeExcel(
    context: Context,
    start: Date,
    end: Date,
    users: HashMap<String, User>,
    callback: (XSSFWorkbook) -> Unit
) {
    OvertimeModel().getAllOvertime { overtimes ->
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Lembur")
        val headerRow = sheet.createRow(0)
        val headerCell = headerRow.createCell(0)
        headerCell.setCellValue("Lembur")
        val row = sheet.createRow(1)
        val cell = row.createCell(0)
        cell.setCellValue("Nama")
        val cell2 = row.createCell(1)
        cell2.setCellValue("Tanggal")
        val cell3 = row.createCell(2)
        cell3.setCellValue("Jam Masuk")
        val cell4 = row.createCell(3)
        cell4.setCellValue("Jam Keluar")
        val cell5 = row.createCell(4)
        cell5.setCellValue("Durasi")
        val cell6 = row.createCell(5)
        cell6.setCellValue("Status")
        var i = 2
        for (overtime in overtimes) {
            val user = users[overtime.userId]!!
            val row = sheet.createRow(i)
            val cell = row.createCell(0)
            cell.setCellValue(user.name)
            val cell2 = row.createCell(1)
            cell2.setCellValue(overtime.date.string())
            val cell3 = row.createCell(2)
            cell3.setCellValue(overtime.date.time.string())
            val cell4 = row.createCell(3)
            cell4.setCellValue(overtime.end.string())
            val cell5 = row.createCell(4)
            cell5.setCellValue("${overtime.start.distance(overtime.end)} jam")
            val cell6 = row.createCell(5)
            cell6.setCellValue(overtime.status.name)
            i++
        }
        callback(workbook)
    }
}