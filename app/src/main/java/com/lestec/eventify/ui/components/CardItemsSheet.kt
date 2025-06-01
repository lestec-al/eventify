package com.lestec.eventify.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lestec.eventify.R
import com.lestec.eventify.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardItemsSheet(
    onDismiss: () -> Unit,
    vm: MainViewModel,
    isVisible: Boolean
) {
    if (isVisible) {
        val context = LocalContext.current

        ModalBottomSheet(onDismissRequest = onDismiss) {
            //
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = context.getString(R.string.events),
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { vm.updateEditSheetOpen(true) }) {
                    Icon(imageVector = Icons.Outlined.AddCircleOutline, contentDescription = null)
                }
            }
            Spacer(Modifier.height(10.dp))
            //
            vm.eventTypes.forEach {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                vm.createEventEntry(it)
                                onDismiss()
                            },
                            onLongClick = { vm.updateEditSheetOpen(true, it) }
                        ),
                    colors = CardColors(
                        containerColor = Color(it.color),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.text,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(Icons.Default.EventAvailable, null)
                    }
                }
            }
            if (vm.eventTypes.isEmpty()) {
                EmptyBox()
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}