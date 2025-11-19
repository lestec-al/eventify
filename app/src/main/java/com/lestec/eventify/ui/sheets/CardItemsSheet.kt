package com.lestec.eventify.ui.sheets

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import com.lestec.eventify.ui.CreatedType
import com.lestec.eventify.ui.MainViewModel
import com.lestec.eventify.ui.components.BaseSheet
import com.lestec.eventify.ui.components.EmptyBox

@Composable
fun CardItemsSheet(vm: MainViewModel) {
    if (vm.cardItemsOpen) {
        val context = LocalContext.current
        BaseSheet(
            onDismiss = vm::updateCardItemsOpen,
            title = context.getString(R.string.event_types),
            actionsRight = {
                listOf(CreatedType.Type, CreatedType.Entry).forEach {
                    IconButton(onClick = { vm.updateEditSheetOpen(true, it) }) {
                        Icon(
                            imageVector = when (it) {
                                CreatedType.Type -> Icons.Outlined.BookmarkAdd
                                CreatedType.Entry -> Icons.Outlined.AddTask
                            },
                            contentDescription = context.getString(
                                when (it) {
                                    CreatedType.Type -> R.string.add_type
                                    CreatedType.Entry -> R.string.add_entry
                                }
                            )
                        )
                    }
                }
            },
        ) {
            Spacer(Modifier.height(10.dp))
            vm.eventTypes.forEach {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                vm.createEventEntry(it.id, it.color, it.text)
                                vm.updateCardItemsOpen()
                            },
                            onLongClick = { vm.updateEditSheetOpen(true, CreatedType.Type, it) }
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
                        Icon(Icons.Outlined.AddTask, null)
                    }
                }
            }
            if (vm.eventTypes.isEmpty()) {
                EmptyBox()
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}