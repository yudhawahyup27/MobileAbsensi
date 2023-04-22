package com.nairobi.absensi.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.R
import com.nairobi.absensi.dashboard.DashboardAdminActivity
import com.nairobi.absensi.dashboard.DashboardUserActivity
import com.nairobi.absensi.ui.components.FormField
import com.nairobi.absensi.ui.theme.AbsensiTheme
import com.nairobi.absensi.ui.theme.Purple
import com.nairobi.absensi.utils.validateEmail
import com.nairobi.absensi.utils.validateLength

// Login screen
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildContent()
    }

    override fun onStart() {
        super.onStart()
        checkState()
    }

    // Check if user is already logged in
    private fun checkState() {
        if (Auth.isLoggedIn()) {
            // Go to dashboard
            if (Auth.getUser()!!.isAdmin()) {
                val intent = Intent(this, DashboardAdminActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, DashboardUserActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }

    // Build login screen
    private fun buildContent() {
        setContent {
            // Theme
            AbsensiTheme {
                // Content
                LoginCompose()
            }
        }
    }

    // Compose login screen
    @Composable
    fun LoginCompose() {
        // Constraint layout
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .paint(painterResource(R.drawable.bg_login), contentScale = ContentScale.FillBounds)
        ) {
            val (form, footer, fab) = createRefs()
            // Login form
            LoginForm(
                modifier = Modifier
                    .constrainAs(form) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, 80.dp)
                    }
            )
            // Footer text
            Text(
                "Hubungi petugas untuk\nmengetahui password anda",
                color = Purple,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .constrainAs(footer) {
                        top.linkTo(form.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
            // Register fab
            FloatingActionButton(
                onClick = { /*TODO*/ },
                shape = MaterialTheme.shapes.large.copy(
                    CornerSize(percent = 50)
                ),
                containerColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 20.dp, end = 20.dp)
                    .constrainAs(fab) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                // Whatsapp icon
                Icon(
                    painterResource(R.drawable.ic_whatsapp),
                    contentDescription = null,
                    tint = Color.Black,
                )
            }
        }
    }

    // Login form
    @Composable
    fun LoginForm(modifier: Modifier) {
        val emptyEmailError = "email tidak boleh kosong"
        val invalidEmailError = "invalid email"
        val emptyPasswordError = "password tidak boleh kosong"
        var email by remember { mutableStateOf(TextFieldValue("admin@admin.com")) }
        var password by remember { mutableStateOf(TextFieldValue("admin0")) }
        var emailError by remember { mutableStateOf(false) }
        var passwordError by remember { mutableStateOf(false) }
        var emailErrorString by remember { mutableStateOf(emptyEmailError) }
        var passwordErrorString by remember { mutableStateOf(emptyPasswordError) }
        val btnEnabled =
            email.text.isNotEmpty() && password.text.isNotEmpty() && !emailError && !passwordError
        // Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // Form title
            Text(
                text = "Toko sparepart motor anji",
                color = Purple,
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                modifier = Modifier
                    .padding(20.dp)
            )
            // Email
            FormField(
                value = email,
                onValueChange = {
                    email = it
                    if (it.text.isEmpty()) {
                        emailError = true
                        emailErrorString = emptyEmailError
                    } else if (!validateEmail(it.text)) {
                        emailError = true
                        emailErrorString = invalidEmailError
                    } else {
                        emailError = false
                    }
                },
                keyboardType = KeyboardType.Email,
                colorStyle = Purple,
                label = "Email",
                leadingIcon = Icons.Default.Person,
                errorMessage = emailErrorString,
                isError = emailError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
            // Password
            FormField(
                value = password,
                onValueChange = {
                    password = it
                    if (it.text.isEmpty()) {
                        passwordError = true
                        passwordErrorString = emptyPasswordError
                    } else if (!validateLength(it.text, 6)) {
                        passwordError = true
                        passwordErrorString = "password harus minimal 6 karakter"
                    } else {
                        passwordError = false
                    }
                },
                colorStyle = Purple,
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                isError = passwordError,
                errorMessage = passwordErrorString,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
            )
            // Submit button
            Button(
                onClick = {
                    val loading =
                        SweetAlertDialog(this@LoginActivity, SweetAlertDialog.PROGRESS_TYPE)
                            .setTitleText("Loading")
                    loading.setCancelable(false)
                    loading.show()

                    Auth.login(email.text, password.text) {
                        if (it != null) {
                            loading.dismiss()
                            SweetAlertDialog(this@LoginActivity, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Sukses")
                                .setConfirmClickListener {
                                    checkState()
                                }
                                .show()
                        } else {
                            loading.dismiss()
                            SweetAlertDialog(this@LoginActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Autentikasi gagal")
                                .setConfirmButtonBackgroundColor(cn.pedant.SweetAlert.R.color.red_btn_bg_color)
                                .show()
                        }
                    }
                },
                enabled = btnEnabled,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color.Gray,
                    containerColor = Purple,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Masuk")
            }
        }
    }
}