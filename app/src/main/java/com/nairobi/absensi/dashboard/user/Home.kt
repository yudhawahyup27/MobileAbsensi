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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.R
import com.nairobi.absensi.auth.Auth

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
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(125.dp)
    ) {
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
@Preview
@Composable
fun DashboardUserHome(navController: NavController? = null) {
    val context = LocalContext.current

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
            // Setting
            IconButton(
                onClick = {
                    navController?.navigate("profile")
                },
                modifier = Modifier
                    .constrainAs(setting) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Setting",
                )
            }
            // Logout
            IconButton(
                onClick = {
                    SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Apakah anda yakin?")
                        .setContentText("Anda akan keluar dari aplikasi")
                        .setConfirmText("Ya")
                        .setConfirmClickListener { sDialog ->
                            sDialog.dismissWithAnimation()
                            Auth.logout()
                        }
                        .setCancelButton(
                            "Tidak"
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
                    contentDescription = "Logout",
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
                "Selamat Datang",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
            // Subtitle
            Text(
                "Absensi membutuhkan info lokasi dan verifikasi wajah.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
            // Navigations
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Masuk
                CardButton(
                    onClick = {
                        navController?.navigate("work")
                    },
                    label = "Absen Masuk",
                    color = Color.Green,
                    background = R.drawable.ic_masuk,
                    modifier = Modifier
                )
                // Lembur
                CardButton(
                    onClick = {
                        navController?.navigate("absen_lembur")
                    },
                    label = "Absen Lembur",
                    color = Color.Yellow,
                    background = R.drawable.ic_lembur,
                    modifier = Modifier
                        .padding(top = 40.dp)
                )
                // Cuti
                CardButton(
                    onClick = {
                        navController?.navigate("absen_izin")
                    },
                    label = "Izin Keluar",
                    color = Color.Blue,
                    background = R.drawable.ic_izin,
                    modifier = Modifier
                        .padding(top = 40.dp)
                )
                // Riwayat
                CardButton(
                    onClick = {
                        navController?.navigate("riwayat")
                    },
                    label = "Riwayat Absen",
                    color = Color.Red,
                    background = R.drawable.ic_history,
                    modifier = Modifier
                        .padding(top = 40.dp)
                )
            }
        }
    }
}
