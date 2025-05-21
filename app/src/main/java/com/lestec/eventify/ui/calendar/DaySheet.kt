package com.lestec.eventify.ui.calendar

import android.text.format.DateFormat
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import com.lestec.eventify.ui.EmptyBox
import com.lestec.eventify.ui.MainViewModel
import com.lestec.eventify.ui.formatMillsTime
import com.lestec.eventify.ui.pxToDp
import java.util.Date
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaySheet(
    onDismissRequest: () -> Unit,
    visible: Boolean,
    vm: MainViewModel
) {
    if (visible) {
        val context = LocalContext.current
        val halfScreenWidth = LocalWindowInfo.current.containerSize.width / 2
        val haptic = LocalHapticFeedback.current

        ModalBottomSheet(onDismissRequest = onDismissRequest) {
            // Date
            Text(
                text = DateFormat.getLongDateFormat(context).format(Date(vm.timeForDay)),
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(8.dp))
            // Stats
            vm.dataForDay.forEach {
                val color = Color(it.color)
                var offsetX by remember { mutableFloatStateOf(0f) }
                var hapticIsPerformed by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .fillMaxWidth()
                ) {
                    // Delete container
                    Card(
                        modifier = Modifier.width(offsetX.pxToDp()),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.error
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
                                    color = color.copy(alpha = 0.6f),
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
            Spacer(Modifier.height(8.dp))
        }
    }
}