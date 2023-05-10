package com.nairobi.absensi.dashboard.admin

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.Overtime
import com.nairobi.absensi.types.OvertimeModel
import com.nairobi.absensi.types.OvertimeStatus
import com.nairobi.absensi.types.User
import com.nairobi.absensi.types.UserModel
import com.nairobi.absensi.ui.components.FormField
import com.nairobi.absensi.ui.components.SimpleAppbar
import com.nairobi.absensi.ui.theme.Orange
import com.nairobi.absensi.ui.theme.Purple

// Manage overtime
@Composable
fun ManageOvertime(navController: NavController? = null) {
    val context = LocalContext.current
    val overtimes = remember { mutableStateOf(ArrayList<Overtime>()) }
    val filter = remember { mutableStateOf(Date()) }
    val users = remember { mutableStateOf(HashMap<String, User>()) }
    var searchValue by remember { mutableStateOf(TextFieldValue()) }

    LaunchedEffect("overtime") {
        UserModel().getUsers({ true }) {
            it.forEach { user ->
                users.value[user.id] = user
            }
            OvertimeModel().getAllOvertime { datas ->
                overtimes.value = datas
            }
        }
    }

    // Column
    ConstraintLayout(
        Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        val (appBar, searchField, content, fab) = createRefs()

        // Simple appbar
        SimpleAppbar(
            navController = navController,
            title = context.getString(R.string.lembur),
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
                .constrainAs(appBar) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
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
                .constrainAs(searchField) {
                    start.linkTo(parent.start)
                    top.linkTo(appBar.bottom)
                    end.linkTo(parent.end)
                }
        )
        // Column
        Column(
            Modifier
                .fillMaxWidth()
                .constrainAs(content) {
                    start.linkTo(parent.start)
                    top.linkTo(searchField.bottom)
                    end.linkTo(parent.end)
                }
                .verticalScroll(rememberScrollState())
        ) {
            overtimes.value
                .filter { it.date == filter.value }
                .filter {
                    val user = users.value[it.userId]
                    if (user != null) {
                        user.name.contains(searchValue.text, true) ||
                                user.email.contains(searchValue.text, true)
                    } else {
                        true
                    }
                }
                .forEach { overtime ->
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
                                Text(users.value[overtime.userId]?.name.toString())
                                Text(users.value[overtime.userId]?.email.toString())
                                Text(overtime.date.string(true))
                                Text("${overtime.start.distanceHour(overtime.end)} jam")
                            }
                            val status: Pair<Color, String> = when (overtime.status) {
                                OvertimeStatus.REJECTED -> Pair(
                                    Color.Red,
                                    context.getString(R.string.ditolak)
                                )

                                OvertimeStatus.PENDING -> Pair(
                                    Orange,
                                    context.getString(R.string.pending)
                                )

                                OvertimeStatus.APPROVED -> Pair(
                                    Color.Green,
                                    context.getString(R.string.disetujui)
                                )
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
        // Fab
        FloatingActionButton(
            onClick = {
                navController?.navigate(context.getString(R.string.add_overtime))
            },
            containerColor = Color.White,
            contentColor = Color.Black,
            modifier = Modifier
                .constrainAs(fab) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .padding(20.dp)
        ) {
            Icon(
                Icons.Default.Add,
                null
            )
        }
    }
}