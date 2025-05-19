package com.lestec.eventify.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lestec.eventify.R
import com.lestec.eventify.ui.EditTextSheet
import com.lestec.eventify.ui.MainViewModel

@Composable
fun CardItems(
    modifier: Modifier,
    vm: MainViewModel
) {
    val context = LocalContext.current

    if (vm.editBottomSheetOpen) {
        EditTextSheet(
            onDismiss = { vm.updateEditBottomSheetOpen(false) },
            onSave = { color, text ->
                vm.createEventType(color, text)
                vm.updateEventTypes()
                vm.updateEditBottomSheetOpen(false)
            }
        )
    }

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        // Upper row with text & actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info text
            Text(
                text = context.getString(R.string.events),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.titleMedium
            )
            // Add card button
            IconButton(onClick = { vm.updateEditBottomSheetOpen(true) }) {
                Icon(Icons.Default.AddCircle, null)
            }
        }
        // Show buttons to all cards
        Spacer(Modifier.height(1.dp))
        vm.eventTypes.forEach {
            Card(
                onClick = {
                    vm.createEventEntry(it)
                    vm.get3MonthsData(vm.today, null)
                },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth(),
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
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        //if (vm.data.isEmpty()) {}
        Spacer(Modifier.height(4.dp))
    }
}