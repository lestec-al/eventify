package com.lestec.eventify.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lestec.eventify.R
import androidx.compose.ui.res.stringResource

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
            val colorStr = stringResource(R.string.color)
            if (iconId != null) {
                Icon(painterResource(iconId), colorStr)
            } else {
                Icon(painterResource(R.drawable.ic_check), colorStr)
            }
        }
    }
}