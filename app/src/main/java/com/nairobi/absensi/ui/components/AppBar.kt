package com.nairobi.absensi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.nairobi.absensi.R

// Simple app bar with title and back button
@Composable
fun SimpleAppbar(
    navController: NavController? = null,
    title: String = "",
    background: Color = Color.Transparent,
    modifier: Modifier
) {
    // Row
    ConstraintLayout(
        modifier
            .fillMaxWidth()
            .background(background)
    ) {
        val (titleText, back) = createRefs()
        // Back button
        IconButton(
            onClick = {
                navController?.navigateUp()
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = Color.White
            ),
            modifier = Modifier.constrainAs(back) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        ) {
            Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = null)
        }
        // Title
        Text(
            text = title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.constrainAs(titleText) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        )
    }
}