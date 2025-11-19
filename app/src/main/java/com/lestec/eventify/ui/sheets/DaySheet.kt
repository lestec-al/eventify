package com.lestec.eventify.ui.sheets

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.lestec.eventify.R
import com.lestec.eventify.ui.MainViewModel
import com.lestec.eventify.ui.components.BaseSheet
import com.lestec.eventify.ui.components.EmptyBox
import com.lestec.eventify.ui.formatMillsDate
import com.lestec.eventify.ui.formatMillsTime
import java.util.Calendar
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaySheet(vm: MainViewModel) {
    if (vm.isShowDayDialog) {
        val context = LocalContext.current
        val halfScreenWidth = LocalWindowInfo.current.containerSize.width / 2
        val haptic = LocalHapticFeedback.current
        var isTimeEdit by remember { mutableStateOf(false) }
        val timePickerState = rememberTimePickerState(
            initialHour = vm.daySheetDate.get(Calendar.HOUR_OF_DAY),
            initialMinute = vm.daySheetDate.get(Calendar.MINUTE),
            is24Hour = true
        )

        BaseSheet(
            onDismiss = vm::setIsShowDayDialog,
            title = vm.timeForDay.formatMillsDate(context) + ",",
            upActions = if (!isTimeEdit) null else {
                {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                    ) {
                        TimeInput(state = timePickerState)
                        OutlinedButton(
                            onClick = {
                                vm.updateCardItemsDateTime(timePickerState)
                                isTimeEdit = false
                            },
                            modifier = Modifier.padding(15.dp)
                        ) {
                            Icon(Icons.Outlined.Check, "ok")
                        }
                    }
                }
            },
            actionsLeft = {
                TextButton(onClick = { isTimeEdit = !isTimeEdit }) {
                    Text(
                        text = vm.getTime(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            actionsRight = {
                IconButton(
                    onClick = {
                        vm.updateCardItemsOpen(true)
                        vm.setIsShowDayDialog()
                    }
                ) {
                    Icon(Icons.Outlined.AddTask, context.getString(R.string.add_entry))
                }
            }
        ) {
            Spacer(Modifier.height(10.dp))
            // Stats
            vm.dataForDay.forEach {
                val color = Color(it.color)
                var offsetX by remember { mutableFloatStateOf(0f) }
                var hapticIsPerformed by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // Delete container
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (offsetX != 0f) MaterialTheme.colorScheme.error else Color.Unspecified
                        ),
                        content = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp)
                                        .size(32.dp)
                                )
                                // This is used to set equal size for delete and item containers ???
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(text = "", style = MaterialTheme.typography.titleLarge)
                                    Text(text = "")
                                }
                            }
                        }
                    )
                    // Item container
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Swipe to delete
                            .offset { IntOffset(offsetX.roundToInt(), 0) }
                            .draggable(
                                orientation = Orientation.Horizontal,
                                state = rememberDraggableState { delta ->
                                    val offsetTemp = offsetX + delta
                                    if (offsetTemp > 0) offsetX = offsetTemp
                                    // Haptic feedback
                                    if (offsetX > halfScreenWidth && !hapticIsPerformed) {
                                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                        hapticIsPerformed = true
                                    } else if (offsetX < halfScreenWidth) {
                                        hapticIsPerformed = false
                                    }
                                },
                                onDragStopped = { _ ->
                                    if (offsetX > halfScreenWidth) {
                                        vm.deleteEventEntry(it)
                                    }
                                    offsetX = 0f
                                    hapticIsPerformed = false
                                }
                            ),
                        colors = CardDefaults.cardColors(),
                        content = {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = it.text,
                                    color = color,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = it.date.formatMillsTime(context),
                                    color = color.copy(alpha = 0.8f),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    )
                }
            }
            if (vm.dataForDay.isEmpty()) {
                EmptyBox()
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}