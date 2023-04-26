package com.nairobi.absensi.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.nairobi.absensi.MapPick
import com.nairobi.absensi.types.Address
import com.nairobi.absensi.types.Date
import com.nairobi.absensi.types.Time

// FormField
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    leadingIcon: ImageVector? = null,
    color: Color = Color.Black,
    colorStyle: Color = Color.Black,
    errorMessage: String = "",
    isError: Boolean = false,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    var isToggled by remember { mutableStateOf(false) }
    var transformation = VisualTransformation.None
    if (isPassword) {
        transformation =
            if (!isToggled) PasswordVisualTransformation() else VisualTransformation.None
    }

    // OutlinedTextField
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = color,
            focusedBorderColor = colorStyle,
            focusedLeadingIconColor = colorStyle,
            focusedLabelColor = colorStyle,
            cursorColor = colorStyle,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        label = {
            label?.let {
                Text(it)
            }
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    it,
                    contentDescription = null,
                )
            }
        },
        trailingIcon = if (isPassword) {
            {
                Icon(
                    if (!isToggled) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable(
                            true,
                            onClick = {
                                isToggled = !isToggled
                            }
                        )
                )
            }
        } else null,
        visualTransformation = transformation,
        supportingText = {
            if (isError) Text(text = errorMessage)
        },
        isError = isError,
        modifier = modifier,
    )
}

// Form field date picker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFieldDate(
    value: Date,
    onValueChange: (Date) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    color: Color = Color.Black,
) {
    val context = LocalContext.current

    OutlinedTextField(
        value = value.string(),
        onValueChange = {},
        readOnly = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = color,
            disabledBorderColor = color,
            disabledLeadingIconColor = color,
            disabledLabelColor = color,
            disabledTextColor = color,
            disabledPlaceholderColor = color,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        label = {
            label?.let {
                Text(it)
            }
        },
        leadingIcon = {
            Icon(
                Icons.Default.DateRange,
                contentDescription = null,
            )
        },
        enabled = false,
        modifier = modifier
            .clickable {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val date = Date()
                        date.year = year
                        date.month = month
                        date.day = dayOfMonth
                        onValueChange(date)
                    },
                    value.year,
                    value.month,
                    value.day
                ).show()
            },
    )
}

// Form field time picker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFieldTime(
    value: Time,
    onValueChange: (Time) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    color: Color = Color.Black,
) {
    val context = LocalContext.current

    OutlinedTextField(
        value = value.string(),
        onValueChange = {},
        readOnly = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = color,
            disabledBorderColor = color,
            disabledLeadingIconColor = color,
            disabledLabelColor = color,
            disabledTextColor = color,
            disabledPlaceholderColor = color,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        label = {
            label?.let {
                Text(it)
            }
        },
        leadingIcon = {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
            )
        },
        enabled = false,
        modifier = modifier
            .clickable {
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val time = Time()
                        time.hour = hourOfDay
                        time.minute = minute
                        onValueChange(time)
                    },
                    value.hour,
                    value.minute,
                    true
                ).show()
            },
    )
}

// Form field location picker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFieldLocation(
    value: Address,
    onValueChange: (Address) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    color: Color = Color.Black,
) {
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val lat = it.data?.getDoubleExtra("lat", 0.0) ?: 0.0
            val long = it.data?.getDoubleExtra("long", 0.0) ?: 0.0
            val address = Address(lat, long)
            onValueChange(address)
        }

    OutlinedTextField(
        value = value.string(context),
        onValueChange = {},
        readOnly = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = color,
            disabledBorderColor = color,
            disabledLeadingIconColor = color,
            disabledLabelColor = color,
            disabledTextColor = color,
            disabledPlaceholderColor = color,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        label = {
            label?.let {
                Text(it)
            }
        },
        leadingIcon = {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
            )
        },
        enabled = false,
        modifier = modifier
            .clickable {
                // get result from activity
                launcher.launch(
                    MapPick.intent(context as ComponentActivity, value.latitude, value.longitude)
                )
            },
    )
}

// Form image picker gallery
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFieldImage(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    color: Color = Color.Black,
) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uri = it.data?.data
            onValueChange(
                uri.toString()
            )
        }

    OutlinedTextField(
        value = TextFieldValue(text = value),
        onValueChange = {},
        readOnly = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = color,
            disabledBorderColor = color,
            disabledLeadingIconColor = color,
            disabledLabelColor = color,
            disabledTextColor = color,
            disabledPlaceholderColor = color,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        label = {
            label?.let {
                Text(it)
            }
        },
        leadingIcon = {
            Icon(
                Icons.Default.Image,
                contentDescription = null,
            )
        },
        enabled = false,
        modifier = modifier
            .clickable {
                // get result from activity
                launcher.launch(
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                )
            },
    )
}

// Form search field
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFieldSearch(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier,
    label: String = "",
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        shape = MaterialTheme.shapes.small.copy(CornerSize(30.dp)),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            textColor = Color.Black,
            focusedLeadingIconColor = Color.Gray,
            unfocusedLeadingIconColor = Color.Gray,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray,
        ),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
            )
        },
        modifier = modifier
            .fillMaxWidth(),
        label = {
            Text(label)
        },
    )
}