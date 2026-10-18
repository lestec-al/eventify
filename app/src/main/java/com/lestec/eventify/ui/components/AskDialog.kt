package com.lestec.eventify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lestec.eventify.R

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
                Text(
                    text = text,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = cancelClicked,
                        shape = CardDefaults.shape,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .weight(1f)
                    ) {
                        Icon(painterResource(R.drawable.ic_close), "close")
                    }
                    OutlinedButton(
                        onClick = confirmButtonCLicked,
                        shape = CardDefaults.shape,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .weight(1f)
                    ) {
                        Icon(painterResource(R.drawable.ic_check), "ok")
                    }
                }
            }
        }
    }
}