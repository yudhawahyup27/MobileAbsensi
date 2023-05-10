package com.nairobi.absensi.dashboard.user

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.R
import com.nairobi.absensi.types.Auth
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.Overtime
import com.nairobi.absensi.types.OvertimeModel
import com.nairobi.absensi.types.OvertimeStatus
import com.nairobi.absensi.ui.components.dialogSuccess
import com.nairobi.absensi.ui.theme.Orange
import com.nairobi.absensi.ui.theme.Pink
import com.nairobi.absensi.ui.theme.Purple

// Card button
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardButton(
    onClick: () -> Unit,
    color: Color,
    label: String,
    @DrawableRes background: Int,
    modifier: Modifier = Modifier
) {
    // Card
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(125.dp)
    ) {
        // Layout
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .paint(
                    painter = painterResource(background),
                    contentScale = ContentScale.Crop
                )
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            color,
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp)
        ) {
            val (title, icon) = createRefs()
            // Title
            Text(
                label,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )
            // Icon
            Box(
                Modifier
                    .constrainAs(icon) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .background(
                        Color.White,
                        shape = MaterialTheme.shapes.large.copy(CornerSize(100))
                    )
                    .padding(10.dp)
            ) {
                Icon(Icons.Default.ArrowForwardIos, contentDescription = null)
            }
        }
    }
}

// Home Screen for User
@Composable
fun DashboardUserHome(navController: NavController? = null) {
    val context = LocalContext.current
    val user = Auth.user!!

    LaunchedEffect(context.getString(R.string.checkAbsence)) {
        OvertimeModel().getOvertimeByUserId(user.id) {overtimes ->
            val pending = overtimes.filter { it.status == OvertimeStatus.PENDING }
            val mayBeNeedReject = ArrayList<Overtime>()
            pending.forEach { x ->
                if (Date().after(x.date)) {
                    x.status = OvertimeStatus.REJECTED
                    mayBeNeedReject.add(x)
                }
            }
            OvertimeModel().updateMultipleOvertime(mayBeNeedReject) {
                val today = pending.find { it.date.isToday() }
                if (today != null) {
                    dialogSuccess(
                        context,
                        "Pemberituan",
                        "Anda memiliki lembur hari ini jam ${today.start.string()}, silahkan lakukan absensi lembur"
                    )
                }
            }
        }
    }

    // Layout
    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val (bar, content) = createRefs()
        // Top Bar
        ConstraintLayout(
            Modifier
                .background(Color.Transparent)
                .fillMaxWidth()
                .constrainAs(bar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            val (setting, logout) = createRefs()
            // Logout
            IconButton(
                onClick = {
                    SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(context.getString(R.string.keluar))
                        .setContentText(context.getString(R.string.keluar_prompt))
                        .setConfirmText(context.getString(R.string.ya))
                        .setConfirmClickListener { sDialog ->
                            sDialog.dismissWithAnimation()
                            Auth.logout()
                        }
                        .setCancelButton(
                            context.getString(R.string.tidak)
                        ) { sDialog -> sDialog.dismissWithAnimation() }
                        .show()
                },
                modifier = Modifier
                    .constrainAs(logout) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Icon(
                    Icons.Filled.Logout,
                    contentDescription = context.getString(R.string.keluar),
                )
            }
        }
        // Content
        Column(
            Modifier
                .fillMaxSize()
                .constrainAs(content) {
                    top.linkTo(bar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .padding(20.dp)
        ) {
            // Title
            Text(
                context.getString(R.string.selamat_datang),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
            // Subtitle
            Text(
                context.getString(R.string.user_home_header),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
            // Navigation
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Masuk
                CardButton(
                    onClick = {
                        navController?.navigate(context.getString(R.string.work))
                    },
                    label = context.getString(R.string.absen_masuk),
                    color = Color.Green,
                    background = R.drawable.ic_masuk,
                    modifier = Modifier
                )
                CardButton(
                    onClick = {
                        navController?.navigate(context.getString(R.string.out))
                    },
                    label = context.getString(R.string.absen_keluar),
                    color = Purple,
                    background = R.drawable.ic_masuk,
                    modifier = Modifier
                        .padding(top = 40.dp)
                )
                // Lembur
                CardButton(
                    onClick = {
                        navController?.navigate(context.getString(R.string.overtime))
                    },
                    label = context.getString(R.string.absen_lembur),
                    color = Color.Blue,
                    background = R.drawable.ic_lembur,
                    modifier = Modifier
                        .padding(top = 40.dp)
                )
                // Cuti
                CardButton(
                    onClick = {
                        navController?.navigate(context.getString(R.string.leave))
                    },
                    label = context.getString(R.string.absen_cuti),
                    color = Orange,
                    background = R.drawable.ic_izin,
                    modifier = Modifier
                        .padding(top = 40.dp)
                )
                // Riwayat
                CardButton(
                    onClick = {
                        navController?.navigate(context.getString(R.string.history))
                    },
                    label = context.getString(R.string.riwayat_absen),
                    color = Color.Red,
                    background = R.drawable.ic_history,
                    modifier = Modifier
                        .padding(top = 40.dp)
                )
                // Riwayat
                CardButton(
                    onClick = {
                        navController?.navigate(context.getString(R.string.profile))
                    },
                    label = context.getString(R.string.profile),
                    color = Pink,
                    background = R.drawable.ic_masuk,
                    modifier = Modifier
                        .padding(top = 40.dp)
                )
            }
        }
    }
}