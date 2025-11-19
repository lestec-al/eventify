package com.lestec.eventify.ui.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lestec.eventify.R
import com.lestec.eventify.ui.CreatedType
import com.lestec.eventify.ui.MainViewModel
import com.lestec.eventify.ui.components.AskDialog
import com.lestec.eventify.ui.components.BaseSheet
import com.lestec.eventify.ui.components.ColoredBox
import com.lestec.eventify.ui.theme.predefinedColors

@Composable
fun EditSheet(vm: MainViewModel) {
    if (vm.editSheetOpen) {
        val context = LocalContext.current

        AskDialog(
            visible = vm.isDelDialogOpen,
            text = context.getString(R.string.delete_info),
            confirmButtonCLicked = vm::deleteEditedEventType,
            cancelClicked = vm::delDialogUpdate
        )
        BaseSheet(
            onDismiss = vm::updateEditSheetOpen,
            title = context.getString(
                if (vm.whatIsCreated == CreatedType.Entry) R.string.add_entry else {
                    if (vm.editedEventType != null) R.string.edit_type else R.string.add_type
                }
            ),
            actionsRight = {
                (1..2).forEach {
                    if (it == 2 || (it == 1 && vm.editedEventType != null)) {
                        IconButton(onClick = if (it == 1) {{ vm.delDialogUpdate(true) }} else vm::editSheetOnSave) {
                            Icon(
                                imageVector = if (it == 1) Icons.Outlined.Delete else Icons.Outlined.Done,
                                contentDescription = if (it == 1) "delete" else "save"
                            )
                        }
                    }
                }
            }
        ) {
            // Input
            OutlinedTextField(
                value = vm.textValue,
                onValueChange = vm::onTextValueChange,
                label = {
                    Text(text = context.getString(R.string.name))
                },
                supportingText = {
                    Text(
                        text = if (vm.textError) context.getString(R.string.event_name_error) else "",
                        color = MaterialTheme.colorScheme.error
                    )
                },
                isError = vm.textError,
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .fillMaxWidth(),
                shape = CardDefaults.shape
            )
            // Color picker
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = context.getString(R.string.color),
                    style = MaterialTheme.typography.titleMedium,
                    color = OutlinedTextFieldDefaults.colors().focusedLabelColor,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    predefinedColors.forEach { c ->
                        ColoredBox(
                            onClick = {
                                vm.updateColor('r', c.red.times(255.0).toFloat())
                                vm.updateColor('g', c.green.times(255.0).toFloat())
                                vm.updateColor('b', c.blue.times(255.0).toFloat())
                            },
                            color = c,
                            isSelected = vm.getWholeColor() == c
                        )
                    }
                    ColoredBox(
                        onClick = vm::createRandomColor,
                        color = null,
                        icon = Icons.Outlined.Shuffle
                    )
                }
                listOf('r', 'g', 'b').forEach { i ->
                    Slider(
                        value = when (i) {
                            'r' -> vm.redColor.toFloat()
                            'g' -> vm.greenColor.toFloat()
                            else -> vm.blueColor.toFloat()
                        },
                        onValueChange = { vm.updateColor(i, it) },
                        valueRange = 0f..255f,
                        colors = SliderDefaults.colors(
                            thumbColor = vm.getWholeColor(),
                            activeTrackColor = vm.getWholeColor()
                        )
                    )
                }
            }
        }
    }
}