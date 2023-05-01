package com.nairobi.absensi.dashboard.admin

import android.content.Context
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Absence
import com.nairobi.absensi.types.AbsenceModel
import com.nairobi.absensi.types.AbsenceType
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.LeaveRequest
import com.nairobi.absensi.types.LeaveRequestModel
import com.nairobi.absensi.types.LeaveRequestStatus
import com.nairobi.absensi.types.User
import com.nairobi.absensi.types.UserModel
import com.nairobi.absensi.ui.components.SimpleAppbar
import com.nairobi.absensi.ui.components.dialogError
import com.nairobi.absensi.ui.components.dialogSuccess
import com.nairobi.absensi.ui.components.loadingDialog
import com.nairobi.absensi.ui.theme.Orange
import com.nairobi.absensi.ui.theme.Purple

// Manage leave
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageLeave(navController: NavController? = null) {
    val context = LocalContext.current
    val leaves = remember { mutableStateOf(ArrayList<LeaveRequest>()) }
    val users = remember { mutableStateOf(HashMap<String, User>()) }

    LaunchedEffect("loaduser") {
        UserModel().getUsers({ true }) {
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
            navController = navController,
            title = context.getString(R.string.cuti),
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
            leaves.value.forEach {
                // Card
                Card(
                    onClick = {
                        if (it.status == LeaveRequestStatus.PENDING) {
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText(context.getString(R.string.cuti))
                                .setConfirmText(context.getString(R.string.setujui))
                                .setCancelText(context.getString(R.string.tolak))
                                .setConfirmClickListener {sDialog ->
                                    sDialog.dismissWithAnimation()
                                    it.status = LeaveRequestStatus.APPROVED
                                    updateRequest(context, it)
                                }
                                .setCancelClickListener {sDialog ->
                                    sDialog.dismissWithAnimation()
                                    it.status = LeaveRequestStatus.REJECTED
                                    updateRequest(context, it)
                                }
                                .show()
                        }
                    },
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
                        // Column
                        Column {
                            Text(users.value[it.userId]?.name.toString())
                            Text(users.value[it.userId]?.email.toString())
                            Text("From: ${it.start.string()}")
                            Text("To: ${it.end.string()}")
                        }
                        val status: Pair<Color, String> = when (it.status) {
                            LeaveRequestStatus.APPROVED -> Pair(
                                Color.Green,
                                context.getString(R.string.disetujui)
                            )

                            LeaveRequestStatus.PENDING -> Pair(
                                Orange,
                                context.getString(R.string.pending)
                            )

                            LeaveRequestStatus.REJECTED -> Pair(
                                Color.Red,
                                context.getString(R.string.ditolak)
                            )
                        }
                        // Status
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
                    // Reason
                    Text(
                        it.reason,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }

    LeaveRequestModel().getLeaveRequests {
        it.sortBy { it.id }
        leaves.value = it
    }
}

private fun updateRequest(context: Context, req: LeaveRequest) {
    val loading = loadingDialog(context)
    loading.show()
    LeaveRequestModel().updateLeaveRequest(req) {status ->
        loading.dismissWithAnimation()
        if (status) {
            if (req.status == LeaveRequestStatus.APPROVED) {
                val range = Date.range(req.start, req.end)
                range.forEach {
                    val absence = Absence()
                    absence.date = it
                    absence.type = AbsenceType.LEAVE
                    absence.userId = req.userId
                    AbsenceModel().addAbsence(absence) {}
                }
            }
            dialogSuccess(
                context,
                context.getString(R.string.sukses),
                context.getString(R.string.disetujui)
            )
        } else {
            dialogError(
                context,
                context.getString(R.string.gagal),
                context.getString(R.string.kesalahan_sistem)
            )
        }
    }
}