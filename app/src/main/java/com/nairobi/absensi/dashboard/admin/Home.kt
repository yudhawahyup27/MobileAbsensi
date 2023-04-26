package com.nairobi.absensi.dashboard.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.ui.theme.DarkGray
import com.nairobi.absensi.ui.theme.Orange
import com.nairobi.absensi.ui.theme.Purple40

// Card Button
@Composable
fun CardButton(name: String, icon: ImageVector, color: Color, modifier: Modifier) {
    // Card
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
        modifier = modifier
            .padding(horizontal = 10.dp)
    ) {
        // Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(20.dp)
        ) {
            // Icon
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .background(
                        color,
                        shape = CircleShape
                    )
                    .padding(10.dp)
                    .size(50.dp)
            )

            Text(
                name,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp)
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        color,
                        shape = CircleShape
                    )
            )

            Text(
                name,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// Home Screen for Admin
@Preview
@Composable
fun DashboardAdminHome(navController: NavController? = null) {
    val context = LocalContext.current
    // Column
    Column(
        Modifier
            .background(Color.White)
            .padding(20.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Row
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(top = 20.dp)
        ) {
            // Manage admin
            CardButton(
                name = context.getString(R.string.admin),
                icon = Icons.Default.Settings,
                color = Color.Red,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navController?.navigate(
                            "${context.getString(R.string.manage_user)}/${
                                context
                                    .getString(R.string.admin)
                                    .uppercase()
                            }"
                        )
                    }
            )
            // Manage user
            CardButton(
                name = context.getString(R.string.karyawan),
                icon = Icons.Default.Person,
                color = Color.Blue,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navController?.navigate(
                            "${context.getString(R.string.manage_user)}/${
                                context
                                    .getString(R.string.user)
                                    .uppercase()
                            }"
                        )
                    }
            )
        }
        // Row
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(top = 20.dp)
        ) {
            // Manage absence
            CardButton(
                name = context.getString(R.string.absensi),
                icon = Icons.Default.DateRange,
                color = Color.Green,
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController?.navigate(context.getString(R.string.manage_absence)) }
            )
            // Manage leave
            CardButton(
                name = context.getString(R.string.cuti),
                icon = Icons.Default.AddTask,
                color = Orange,
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController?.navigate(context.getString(R.string.manage_leave)) }
            )
        }
        // Row
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(top = 20.dp)
        ) {
            // Manage overtime
            CardButton(
                name = context.getString(R.string.lembur),
                icon = Icons.Default.Bedtime,
                color = Purple40,
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController?.navigate(context.getString(R.string.manage_overtime)) }
            )
            // Manage office
            CardButton(
                name = context.getString(R.string.kantor),
                icon = Icons.Default.Domain,
                color = Color.Cyan,
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController?.navigate(context.getString(R.string.manage_office)) }
            )
        }
        // Row
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(vertical = 20.dp)
        ) {
            // Logout
            CardButton(
                name = context.getString(R.string.keluar),
                icon = Icons.Default.Logout,
                color = DarkGray,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(context.getString(R.string.keluar))
                            .setContentText(context.getString(R.string.keluar_prompt))
                            .setConfirmText(context.getString(R.string.ya))
                            .setConfirmClickListener {
                                Auth.logout()
                            }
                            .setCancelText(context.getString(R.string.tidak))
                            .show()
                    }
            )
        }
    }
}