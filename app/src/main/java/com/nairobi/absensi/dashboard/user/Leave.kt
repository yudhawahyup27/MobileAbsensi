package com.nairobi.absensi.dashboard.user

import android.annotation.SuppressLint
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.types.LeaveRequest
import com.nairobi.absensi.types.LeaveRequestModel
import com.nairobi.absensi.types.LeaveRequestStatus
import com.nairobi.absensi.ui.components.SimpleAppbar
import com.nairobi.absensi.ui.theme.Orange
import com.nairobi.absensi.ui.theme.Purple

// Leave
@SuppressLint("MutableCollectionMutableState")
@Composable
fun Leave(navController: NavController? = null) {
    val context = LocalContext.current
    val user = Auth.user!!
    val history = remember { mutableStateOf(ArrayList<LeaveRequest>()) }

    LaunchedEffect("leave") {
        LeaveRequestModel().getLeaveRequestByUser(user.id) {
            history.value = it
        }
    }

    // Layout
    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val (content, addbtn) = createRefs()
        // Column
        Column(
            Modifier
                .background(Color.White)
                .fillMaxSize()
                .constrainAs(content) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            // Simple Appbar
            SimpleAppbar(
                modifier = Modifier
                    .fillMaxWidth(),
                title = context.getString(R.string.cuti),
                background = Purple,
                navController = navController
            )
            // Column
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                history.value.forEach { data ->
                    // Card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Row
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        ) {
                            val (text, color) = when (data.status) {
                                LeaveRequestStatus.PENDING -> context.getString(R.string.pending) to Orange
                                LeaveRequestStatus.REJECTED -> context.getString(R.string.ditolak) to Color.Red
                                LeaveRequestStatus.APPROVED -> context.getString(R.string.disetujui) to Color.Green
                            }
                            Column() {
                                Text("From: ${data.start.string()}")
                                Text("To: ${data.end.string()}")
                            }
                            Text(
                                text,
                                color = Color.White,
                                modifier = Modifier
                                    .background(
                                        color, shape = MaterialTheme.shapes.large.copy(
                                            CornerSize(100)
                                        )
                                    )
                                    .padding(5.dp)
                            )
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                navController?.navigate(context.getString(R.string.leave_request))
            },
            containerColor = Color.White,
            contentColor = Color.Black,
            modifier = Modifier
                .constrainAs(addbtn) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .padding(20.dp),
        ) {
            Icon(Icons.Default.Add, null)
        }
    }
}