package com.nairobi.absensi.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.nairobi.absensi.types.User
import com.nairobi.absensi.types.UserModel
import com.nairobi.absensi.ui.theme.Aqua

// Manage template
@SuppressLint("MutableCollectionMutableState")
@Composable
fun ManageTemplate(
    navController: NavController? = null,
    title: String = "",
    filter: (User) -> Boolean = { true },
    addRoute: String = "",
    editRoute: String = "",
) {
    val context = LocalContext.current
    var searchValue by remember { mutableStateOf(TextFieldValue()) }
    val users = remember { mutableStateOf<ArrayList<User>>(ArrayList()) }

    // Get users
    UserModel().getUsers(filter) {
        users.value = it
    }

    // Constraint layout
    ConstraintLayout(
        Modifier
            .fillMaxSize()
    ) {
        val (main, fab) = createRefs()
        // Column
        Column(
            Modifier
                .background(Aqua)
                .fillMaxSize()
                .padding(16.dp)
                .constrainAs(main) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            // Simple appbar
            SimpleAppbar(
                navController,
                title = title,
                modifier = Modifier
                    .fillMaxWidth()
            )
            // Search field
            FormFieldSearch(
                value = searchValue,
                onValueChange = { searchValue = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
            // Card
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Show filtered data
                users.value.filter {
                    val address = it.address.string(context)
                    it.name.contains(searchValue.text, true)
                            || it.email.contains(searchValue.text, true)
                            || address.contains(searchValue.text, true)
                }.forEach {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                        ),
                        modifier = Modifier
                            .clickable {
                                navController?.navigate("$editRoute/${it.id}")
                            }
                            .fillMaxWidth()
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(it.name)
                            Text(it.email)
                        }
                    }
                }
            }
        }
        // Floating action button
        FloatingActionButton(
            onClick = {
                navController?.navigate(addRoute)
            },
            shape = MaterialTheme.shapes.large.copy(
                CornerSize(percent = 50)
            ),
            containerColor = Color.White,
            contentColor = Aqua,
            modifier = Modifier
                .padding(bottom = 20.dp, end = 20.dp)
                .constrainAs(fab) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }
}