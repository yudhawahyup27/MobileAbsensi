package com.nairobi.absensi.dashboard.admin

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Absence
import com.nairobi.absensi.types.AbsenceModel
import com.nairobi.absensi.types.AbsenceType
import com.nairobi.absensi.types.User
import com.nairobi.absensi.types.UserModel
import com.nairobi.absensi.ui.components.SimpleAppbar
import com.nairobi.absensi.ui.theme.Purple

// Manage absence
@Composable
fun ManageAbsence(navController: NavController? = null) {
    val context = LocalContext.current
    val absences = remember { mutableStateOf(ArrayList<Absence>()) }
    val users = remember { mutableStateOf(HashMap<String, User>()) }

    LaunchedEffect("") {
        UserModel().getUsers({true}) {
            it.forEach { user ->
                users.value[user.id] = user
            }
        }
    }

    // Column
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Simple Appbar
        SimpleAppbar(
            title = context.getString(R.string.absensi),
            background = Purple,
            modifier = Modifier
                .fillMaxWidth()
        )
        // Column
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            absences.value.forEach {
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
                            Text(it.date.string(true))
                        }
                        val status: Pair<Color, String> = when(it.type) {
                            AbsenceType.UNKNOWN -> Color.Gray to context.getString(R.string.bolos)
                            AbsenceType.HOLIDAY -> Color.Green to context.getString(R.string.libur)
                            AbsenceType.LEAVE -> Color.Blue to context.getString(R.string.cuti)
                            AbsenceType.WORK -> Color.Red to context.getString(R.string.kerja)
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