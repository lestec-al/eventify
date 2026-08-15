package com.lestec.eventify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun EmptyBox(
    text: String,
    iconId: Int,
    text2: String? = null,
    iconId2: Int? = null,
) {
    val color = MaterialTheme.colorScheme.outline
    val isTwoRows = text2 != null && iconId2 != null

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        listOf(
            Pair(text, iconId),
            if (isTwoRows) Pair(text2, iconId2) else null,
        ).forEach {
            if (it != null) {
                val texts = it.first.split("+")

                Row {
                    Text(
                        text = texts[0],
                        color = color
                    )
                    Icon(
                        painter = painterResource(it.second),
                        contentDescription = null,
                        tint = color
                    )
                    Text(
                        text = texts[1],
                        color = color
                    )
                }
            }
        }
    }
}