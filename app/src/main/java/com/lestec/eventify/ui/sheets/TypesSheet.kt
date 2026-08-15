package com.lestec.eventify.ui.sheets

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import com.lestec.eventify.R
import com.lestec.eventify.ui.CreatedType
import com.lestec.eventify.ui.MainViewModel
import com.lestec.eventify.ui.components.BaseSheet
import com.lestec.eventify.ui.components.EmptyBox
import com.lestec.eventify.ui.overlaps
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TypesSheet(vm: MainViewModel) {
    if (vm.cardItemsOpen) {
        val addTaskIcon = painterResource(R.drawable.ic_add_task)
        val lazyState = rememberLazyListState()
        var openedPref by remember { mutableStateOf<Int?>(null) }
        var foundId by remember { mutableStateOf<Int?>(null) }

        fun onDragEnd(idx: Int) {
            openedPref = null
            foundId?.also {
                vm.moveType(idx, it)
                foundId = null
            }
        }

        BaseSheet(
            onDismiss = vm::updateCardItemsOpen,
            title = stringResource(R.string.templates),
            titleActionsRight = {
                IconButton(onClick = {
                    vm.updateEditSheet(true, CreatedType.Type)
                    openedPref = null
                }) {
                    Icon(
                        painterResource(R.drawable.ic_bookmark_add),
                        stringResource(R.string.add_template)
                    )
                }
            },
            description = vm.getDateTime(
                stringResource(R.string.add_entry_description),
                LocalContext.current
            )
        ) {
            LazyColumn(
                state = lazyState,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(items = vm.eventTypes) { idx, type ->
                    var y by remember { mutableFloatStateOf(0f) }
                    var z by remember { mutableFloatStateOf(0f) }
                    val interact = remember { MutableInteractionSource() }
                    var thisIt: IntRange? = null
                    var press: PressInteraction.Press? = null

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .indication(
                                interact,
                                LocalIndication.current
                            )
                            .pointerInput(openedPref) {
                                detectTapGestures(
                                    onPress = {
                                        press = PressInteraction.Press(it)
                                        interact.emit(press)
                                        tryAwaitRelease()
                                        interact.emit(PressInteraction.Release(press))
                                    },
                                    onTap = {
                                        if (openedPref != null && openedPref == idx) {
                                            vm.updateEditSheet(
                                                true,
                                                CreatedType.Type,
                                                type
                                            )
                                            openedPref = null
                                        } else if (openedPref != null) {
                                            openedPref = idx
                                        } else {
                                            vm.createEventEntry(type.id, type.color, type.text)
                                            vm.updateCardItemsOpen()
                                        }
                                    },
                                    onLongPress = {
                                        if (openedPref == null || openedPref != idx) {
                                            openedPref = idx
                                        }
                                        vm.viewModelScope.launch {
                                            press?.also { p ->
                                                interact.emit(PressInteraction.Release(p))
                                            }
                                        }
                                    }
                                )
                            }
                            .then(if (openedPref != null && openedPref == idx) {
                                Modifier
                                    .zIndex(z)
                                    .offset { IntOffset(x = 0, y = y.roundToInt()) }
                                    .pointerInput(Unit) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = { z = 1f },
                                            onDragEnd = {
                                                y = 0f
                                                z = 0f
                                                thisIt = null
                                                onDragEnd(idx)
                                            },
                                            onDragCancel = {
                                                y = 0f
                                                z = 0f
                                                thisIt = null
                                                onDragEnd(idx)
                                            },
                                            onDrag = { _, dragAmount ->
                                                y += dragAmount.y
                                                // Check if this item overlaps with other items
                                                lazyState.layoutInfo.visibleItemsInfo.forEach {
                                                    if (idx == it.index) {
                                                        thisIt = (it.offset + y).roundToInt()..((it.offset + it.size) + y)
                                                            .roundToInt()
                                                    } else {
                                                        thisIt?.apply {
                                                            if (
                                                                (it.offset..(it.offset + it.size))
                                                                    .overlaps(this)
                                                            ) {
                                                                foundId = it.index
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    }
                            } else {
                                Modifier
                            }),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(type.color),
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (openedPref != null && openedPref == idx) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_arrows_outward),
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 20.dp)
                                )
                            }
                            Text(
                                text = type.text,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            if (openedPref != null && openedPref == idx) {
                                Icon(Icons.Default.Edit, null)
                            } else {
                                Icon(addTaskIcon, null)
                            }
                        }
                    }
                }
                if (vm.eventTypes.isEmpty()) {
                    item {
                        EmptyBox(
                            text = stringResource(R.string.add_template_info),
                            iconId = R.drawable.ic_bookmark_add,
                            text2 = stringResource(R.string.add_entry_info),
                            iconId2 = R.drawable.ic_add_task
                        )
                    }
                }
                item {
                    OutlinedButton(
                        onClick = {
                            vm.updateEditSheet(true, CreatedType.Entry)
                            openedPref = null
                        },
                        shape = CardDefaults.shape,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            addTaskIcon,
                            stringResource(R.string.add_entry)
                        )
                    }
                }
            }
        }
    }
}