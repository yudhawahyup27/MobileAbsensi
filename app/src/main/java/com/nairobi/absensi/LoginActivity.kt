package com.nairobi.absensi

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nairobi.absensi.dashboard.DashboardAdminActivity
import com.nairobi.absensi.dashboard.DashboardUserActivity
import com.nairobi.absensi.types.Auth
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
        com.nairobi.absensi.utils.checkPermission(this)
    }

    // Check if user is already logged in
    private fun checkState() {
        if (Auth.isLoggedIn()) {
            // Go to dashboard
            if (!Auth.user!!.isAdmin) {
                val intent = Intent(this, DashboardUserActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, DashboardAdminActivity::class.java)
                startActivity(intent)
                finish()
            }
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
        val context = LocalContext.current
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
                context.getString(R.string.login_footer),
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
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.link/wwgcfo"))
                    val title = "Complete Action Using"
                    val chooser = Intent.createChooser(intent, title)
                    startActivity(chooser)
                   },
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
        val context = LocalContext.current

        val emptyEmailError = context.getString(R.string.empty_email)
        val invalidEmailError = context.getString(R.string.invalid_email)
        val emptyPasswordError = context.getString(R.string.empty_password)
        var email by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }
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
                text = context.getString(R.string.login_title),
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
                label = context.getString(R.string.email),
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
                    }
                    else if (!validateLength(it.text, 6)) {
                        passwordError = true
                        passwordErrorString = context.getString(R.string.length_error_password)
                    } else {
                        passwordError = false
                    }
                },
                colorStyle = Purple,
                label = context.getString(R.string.password),
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
                    loading.titleText = context.getString(R.string.loading)
                    loading.setCancelable(false)
                    loading.show()

                    Auth.login(email.text, password.text) {
                        loading.dismissWithAnimation()
                        if (it) {
                            SweetAlertDialog(this@LoginActivity, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText(context.getString(R.string.sukses))
                                .setConfirmClickListener {
                                    checkState()
                                }
                                .show()
                        } else {
                            SweetAlertDialog(this@LoginActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(context.getString(R.string.error))
                                .setContentText(context.getString(R.string.autentikasi_error))
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
                Text(context.getString(R.string.login))
            }
            // Forgot password
            Button(
                onClick = {
                    val intent = Intent(this@LoginActivity, ForgotActivity::class.java)
                    startActivity(intent)
                    this@LoginActivity.finish()
                },
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color.Gray,
                    containerColor = Color.White,
                    contentColor = Purple
                ),
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(context.getString(R.string.forgot_password))
            }
        }
    }
}