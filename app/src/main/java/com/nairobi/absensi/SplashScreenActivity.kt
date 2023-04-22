package com.nairobi.absensi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nairobi.absensi.auth.Auth
import com.nairobi.absensi.auth.LoginActivity
import com.nairobi.absensi.ui.theme.AbsensiTheme
import com.nairobi.absensi.utils.showPermissionDialogAlert

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildContent()
        setFullscreen()
    }

    override fun onStart() {
        super.onStart()
        val granted = com.nairobi.absensi.utils.checkPermission(this)
        if (!granted) {
            showPermissionDialogAlert(this)
        }
        Auth.init(this) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Set fullscreen
    private fun setFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    // Build content for splash screen
    private fun buildContent() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val version = packageInfo.versionName
        setContent {
            // Theme
            AbsensiTheme {
                SplashCompose(version)
            }
        }
    }
}

// Composable splash screen
@Composable
fun SplashCompose(versionName: String) {
    // Constraint layout
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val (logo, title, version) = createRefs()
        // Logo
        Image(
            painter = painterResource(R.drawable.splashscreen),
            contentDescription = null,
            modifier = Modifier
                .size(125.dp)
                .background(Color.Transparent)
                .constrainAs(logo) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        )
        // Title
        Text(
            text = "Absensi",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(logo.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        // Version
        Text(
            text = "version $versionName",
            color = Color.Gray,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .constrainAs(version) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}