package com.lestec.eventify.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lestec.eventify.R

@Composable
fun RowScope.ColoredBox(
    onClick: () -> Unit,
    color: Color?,
    isSelected: Boolean = true,
    iconId: Int? = null
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color ?: Color.Transparent,
            contentColor = if (color == null) MaterialTheme.colorScheme.onBackground else Color.Unspecified
        ),
        contentPadding = PaddingValues(),
        border = if (color == null) ButtonDefaults.outlinedButtonBorder() else null,
        modifier = Modifier
            .padding(2.dp)
            .weight(1f)
    ) {
        if (isSelected) {
            val colorStr = LocalContext.current.getString(R.string.color)
            if (iconId != null) {
                Icon(painterResource(iconId), colorStr)
            } else {
                Icon(Icons.Default.Check, colorStr)
            }
        }
    }
}