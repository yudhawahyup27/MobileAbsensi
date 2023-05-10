package com.nairobi.absensi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.constraintlayout.compose.ConstraintLayout
import com.nairobi.absensi.types.UserModel
import com.nairobi.absensi.ui.components.FormField
import com.nairobi.absensi.ui.components.dialogError
import com.nairobi.absensi.ui.components.dialogSuccess
import com.nairobi.absensi.ui.components.loadingDialog
import com.nairobi.absensi.ui.theme.AbsensiTheme
import com.nairobi.absensi.ui.theme.Purple
import com.nairobi.absensi.utils.validateEmail
import com.nairobi.absensi.utils.validateLength

// Forgot Activity
class ForgotActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildContent()
    }

    // Build Content
    private fun buildContent() {
        setContent {
            // Theme
            AbsensiTheme {
                // Content
                ForgotCompose()
            }
        }
    }

    // Forgot Compose
    @Composable
    fun ForgotCompose() {
        val context = LocalContext.current
        val emptyEmailError = context.getString(R.string.empty_email)
        val invalidEmailError = context.getString(R.string.invalid_email)
        val emptyPasswordError = context.getString(R.string.empty_password)
        var email by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }
        var nip by remember { mutableStateOf(TextFieldValue("")) }
        var emailError by remember { mutableStateOf(false) }
        var passwordError by remember { mutableStateOf(false) }
        var emailErrorString by remember { mutableStateOf(emptyEmailError) }
        var passwordErrorString by remember { mutableStateOf(emptyPasswordError) }
        val btnEnabled =
            email.text.isNotEmpty() && password.text.isNotEmpty() && !emailError && !passwordError && nip.text.isNotEmpty()

        // Constraint Layout
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .paint(painterResource(R.drawable.bg_login), contentScale = ContentScale.FillBounds)
        ) {
            val (form) = createRefs()
            // Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .constrainAs(form) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Title
                Text(
                    text = context.getString(R.string.reset_password),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Purple,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
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
                        } else if (!validateLength(it.text, 6)) {
                            passwordError = true
                            passwordErrorString = context.getString(R.string.length_error_password)
                        } else {
                            passwordError = false
                        }
                    },
                    colorStyle = Purple,
                    label = context.getString(R.string.password_baru),
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    isError = passwordError,
                    errorMessage = passwordErrorString,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                )
                // Text
                Text(
                    text = context.getString(R.string.reset_password_text),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp),
                )
                // Nip
                FormField(
                    value = nip,
                    onValueChange = {
                        nip = it
                    },
                    colorStyle = Purple,
                    label = context.getString(R.string.nip),
                    leadingIcon = Icons.Default.Key,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                )
                // Submit button
                Button(
                    onClick = {
                        val dialog = loadingDialog(context)
                        UserModel().getUser(
                            hashMapOf(
                                "email" to email.text,
                                "nip" to nip.text,
                            )
                        ) {
                            if (it == null) {
                                dialog.dismissWithAnimation()
                                dialogError(
                                    context,
                                    context.getString(R.string.error),
                                    context.getString(R.string.error_reset_password)
                                )
                            } else {
                                it.password = password.text
                                UserModel().updateUser(it) { sukses ->
                                    dialog.dismissWithAnimation()
                                    if (sukses) {
                                        dialogSuccess(
                                            context,
                                            context.getString(R.string.sukses),
                                            context.getString(R.string.success_reset_password)
                                        ) {
                                            val intent = Intent(context, LoginActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                    } else {
                                        dialogError(
                                            context,
                                            context.getString(R.string.error),
                                            context.getString(R.string.error_system)
                                        )
                                    }
                                }
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
                    Text(context.getString(R.string.reset_password))
                }
                // Cancel button
                Button(
                    onClick = {
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
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
                    Text(context.getString(R.string.cancel))
                }
            }
        }
    }
}