package com.nairobi.absensi.dashboard.user

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.mapbox.maps.extension.style.layers.generated.backgroundLayer
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Absence
import com.nairobi.absensi.types.AbsenceModel
import com.nairobi.absensi.types.AbsenceType
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.Office
import com.nairobi.absensi.types.OfficeModel
import com.nairobi.absensi.types.Overtime
import com.nairobi.absensi.types.OvertimeModel
import com.nairobi.absensi.types.OvertimeStatus
import com.nairobi.absensi.ui.components.FormField
import com.nairobi.absensi.ui.components.SimpleAppbar
import com.nairobi.absensi.ui.theme.Orange
import com.nairobi.absensi.ui.theme.Purple

// History
@Composable
fun History(navController: NavController? = null) {
    val context = LocalContext.current
    val user = Auth.user!!
    val filter = remember { mutableStateOf(Date()) }
    val history = remember { mutableStateOf(ArrayList<Absence>()) }
    val overtimes = remember { mutableStateOf(ArrayList<Overtime>()) }
    val office = remember { mutableStateOf(Office()) }

    LaunchedEffect("userhistory") {
        OvertimeModel().getAllOvertime {
            it.forEach { ov ->
                if (ov.userId == user.id) {
                    overtimes.value.add(ov)
                }
            }
        }
        AbsenceModel().getAbsences {
            it.forEach { ab ->
                if (ab.userId == user.id) {
                    history.value.add(ab)
                }
            }
        }
        OfficeModel().getOffice {
            office.value = it
        }
    }

    // Layout
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        // Simple Appbar
        SimpleAppbar(
            navController = navController,
            title = context.getString(R.string.history),
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

        // Column
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            history.value.sortBy { h -> h.date.unix()}
            overtimes.value.sortBy { h -> h.date.unix()}
            history.value
                .forEach { h ->
                // Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Row
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val status: Pair<Color, String> = when(h.type) {
                            AbsenceType.UNKNOWN -> Color.Gray to context.getString(R.string.bolos)
                            AbsenceType.HOLIDAY -> Color.Red to context.getString(R.string.libur)
                            AbsenceType.LEAVE -> Color.Blue to context.getString(R.string.cuti)
                            AbsenceType.OUT -> Color.Blue to context.getString(R.string.out)
                            AbsenceType.WORK -> Color.Green to context.getString(R.string.kerja)
                            AbsenceType.ONWORK -> Color.Green to context.getString(R.string.sedang_kerja)
                        }

                        // Column
                        Column {
                            val d = h.date
                            val t = d.time
                            Text(user.email)
                            Text("Masuk: ${d.string(false)} ${t.string(true)}")
                            if (h.endDate != null) {
                                Text("Keluar: ${h.endDate!!.string(true)}")
                                Text("Durasi: ${h.date.hoursBetween(h.endDate!!)} jam")
                            } else {
                                val end = h.date
                                end.time = office.value.endTime
                                Text("Durasi: ${h.date.hoursBetween(end)} jam")
                            }
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
            overtimes.value
                .forEach { h ->
                    // Card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Row
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val status: Pair<Color, String> = when(h.status) {
                                OvertimeStatus.APPROVED  -> Pair(Color.Green, context.getString(R.string.disetujui))
                                OvertimeStatus.PENDING   -> Pair(Orange, context.getString(R.string.pending))
                                OvertimeStatus.REJECTED  -> Pair(Color.Red, context.getString(R.string.ditolak))
                            }

                            // Column
                            Column {
                                Text(user.email)
                                Text("Masuk: ${h.date.string(true)}")
                                val end = Date()
                                end.time = h.end
                                Text("Keluar: ${end.string(true)}")
                                Text("Durasi: ${h.date.hoursBetween(end)} jam")
                            }
                            Text(
                                "${context.getString(R.string.lembur)} ${status.second}",
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
}