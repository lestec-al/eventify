package com.lestec.eventify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AskDialog(
    visible: Boolean,
    text: String,
    confirmButtonCLicked: () -> Unit,
    cancelClicked: () -> Unit
) {
    if (visible) {
        Dialog(onDismissRequest = cancelClicked) {
            Card {
                // Text
                Text(
                    text = text,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = cancelClicked) {
                        Icon(Icons.Default.Close, "close")
                    }
                    IconButton(onClick = confirmButtonCLicked) {
                        Icon(Icons.Default.Check, "ok")
                    }
                }
            }
        }
    }
}