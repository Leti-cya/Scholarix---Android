package com.example.scholarix.ui.components

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.scholarix.ui.theme.PrimaryBlue

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    IconButton(
        modifier = modifier,
        onClick = {
            if (onBackClick != null) {
                onBackClick()
            } else {
                (context as? Activity)?.finish()
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Back",
            tint = PrimaryBlue
        )
    }
}