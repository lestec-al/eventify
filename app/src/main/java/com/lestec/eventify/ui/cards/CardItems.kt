package com.lestec.eventify.ui.cards

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lestec.eventify.ui.EditSheet
import com.lestec.eventify.ui.EmptyBox
import com.lestec.eventify.ui.MainViewModel

@Composable
fun CardItems(
    modifier: Modifier,
    vm: MainViewModel
) {
    if (vm.editSheetOpen) {
        EditSheet(
            onDismiss = { vm.updateEditSheetOpen(false) },
            onSave = { if (vm.editedEventType != null) vm.updateEventType(it) else vm.createEventType(it) },
            onDelete = { vm.deleteEventType(vm.editedEventType!!) },
            editedEventType = vm.editedEventType
        )
    }

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Spacer(Modifier.height(4.dp))
        vm.eventTypes.forEach {
            Card(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { vm.createEventEntry(it) },
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
                        .fillMaxSize(),
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
            Spacer(Modifier.height(4.dp))
        }
        if (vm.eventTypes.isEmpty()) {
            EmptyBox()
            Spacer(Modifier.height(4.dp))
        }
    }
}