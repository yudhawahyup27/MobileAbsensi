package com.nairobi.absensi.dashboard.admin

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Absence
import com.nairobi.absensi.types.AbsenceModel
import com.nairobi.absensi.types.AbsenceType
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.Office
import com.nairobi.absensi.types.OfficeModel
import com.nairobi.absensi.types.User
import com.nairobi.absensi.types.UserModel
import com.nairobi.absensi.ui.components.FormField
import com.nairobi.absensi.ui.components.SimpleAppbar
import com.nairobi.absensi.ui.theme.Purple

// Manage absence
@SuppressLint("MutableCollectionMutableState")
@Composable
fun ManageAbsence(navController: NavController? = null) {
    val context = LocalContext.current
    val absences = remember { mutableStateOf(ArrayList<Absence>()) }
    val users = remember { mutableStateOf(HashMap<String, User>()) }
    var searchValue by remember { mutableStateOf(TextFieldValue()) }
    val filter = remember { mutableStateOf(Date()) }
    val office = remember { mutableStateOf(Office()) }

    LaunchedEffect("manageabsence") {
        UserModel().getUsers({ true }) {
            it.forEach { user ->
                users.value[user.id] = user
            }
        }
        OfficeModel().getOffice { office.value = it }
    }

    // Column
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Simple Appbar
        SimpleAppbar(
            navController = navController,
            title = context.getString(R.string.absensi),
            background = Purple,
            trailingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null
                )
            },
            trailingOnClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val date = Date()
                        date.year = year
                        date.month = month
                        date.day = dayOfMonth
                        filter.value = date
                    },
                    filter.value.year,
                    filter.value.month - 1,
                    filter.value.day
                ).show()
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        // Search field
        FormField(
            value = searchValue,
            leadingIcon = Icons.Default.Search,
            label = context.getString(R.string.cari),
            onValueChange = { searchValue = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        // Column
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            absences.value.filter { it.date == filter.value }
                .filter {
                    val user = users.value[it.userId]
                    if (user != null) {
                        user.name.contains(searchValue.text, true) ||
                            user.email.contains(searchValue.text, true)
                    } else {
                        true
                    }
                }
                .forEach {
                // Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Row
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(users.value[it.userId]?.name.toString())
                            Text(users.value[it.userId]?.email.toString())
                            Text("Masuk: ${it.date.string(true)}")
                            if (it.endDate != null) {
                                Text("Keluar: ${it.endDate!!.string(true)}")
                                Text("Durasi: ${it.date.hoursBetween(it.endDate!!)} jam")
                            } else {
                                val end = it.date
                                end.time = office.value.endTime
                                Text("Durasi: ${it.date.hoursBetween(end)} jam")
                            }
                        }
                        val status: Pair<Color, String> = when (it.type) {
                            AbsenceType.UNKNOWN -> Color.Gray to context.getString(R.string.bolos)
                            AbsenceType.HOLIDAY -> Color.Red to context.getString(R.string.libur)
                            AbsenceType.LEAVE -> Color.Blue to context.getString(R.string.cuti)
                            AbsenceType.WORK -> Color.Green to context.getString(R.string.kerja)
                             AbsenceType.OUT -> Color.Green to context.getString(R.string.Pulang)
                            AbsenceType.ONWORK -> Color.Green to context.getString(R.string.sedang_kerja)
                        }
                        Text(
                            status.second,
                            color = Color.White,
                            modifier = Modifier
                                .background(
                                    status.first,
                                    shape = MaterialTheme.shapes.large.copy(CornerSize(4.dp))
                                )
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }

    AbsenceModel().getAbsences { data ->
        data.sortBy { it.date.unix() }
        absences.value = data
    }
}