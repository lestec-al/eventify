package com.lestec.eventify.ui

import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.lestec.eventify.R
import com.lestec.eventify.data.EventType
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSheet(
    onDismiss: () -> Unit,
    onSave: (eventType: EventType) -> Unit,
    onDelete: () -> Unit,
    editedEventType: EventType?
) {
    val context = LocalContext.current
    var textValue by remember {
        if (editedEventType != null) mutableStateOf(editedEventType.text) else mutableStateOf("")
    }
    val itemColor = editedEventType?.color?.let { Color(it) } ?: MaterialTheme.colorScheme.primary
    var redColor by remember { mutableIntStateOf(itemColor.toArgb().red) }
    var greenColor by remember { mutableIntStateOf(itemColor.toArgb().green) }
    var blueColor by remember { mutableIntStateOf(itemColor.toArgb().blue) }
    var textError by remember { mutableStateOf(false) }
    // To separate viewModel ???
    fun updateColor(colorType: Char, color: Float) {
        when (colorType) {
            'r' -> redColor = color.toInt()
            'g' -> greenColor = color.toInt()
            'b' -> blueColor = color.toInt()
        }
    }
    fun getWholeColor() = Color(redColor, greenColor, blueColor)
    fun createRandomColor() {
        redColor = Random.nextInt(256)
        greenColor = Random.nextInt(256)
        blueColor = Random.nextInt(256)
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        // Input text
        OutlinedTextField(
            value = textValue,
            onValueChange = { textValue = it },
            label = {
                Text(text = context.getString(R.string.event_name))
            },
            supportingText = {
                Text(
                    text = if (textError) context.getString(R.string.event_name_error) else "",
                    color = MaterialTheme.colorScheme.error
                )
            },
            isError = textError,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            shape = CardDefaults.shape
        )
        Spacer(Modifier.height(10.dp))
        // Color picker
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = context.getString(R.string.color),
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(
                    onClick = ::createRandomColor,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = itemColor)
                ) {
                    Icon(Icons.Default.Edit, null)
                }
            }
            Slider(
                value = redColor.toFloat(),
                onValueChange = { updateColor('r', it) },
                valueRange = 0f..255f,
                colors = SliderDefaults.colors(
                    thumbColor = getWholeColor(),
                    activeTrackColor = getWholeColor()
                )
            )
            Slider(
                value = greenColor.toFloat(),
                onValueChange = { updateColor('g', it) },
                valueRange = 0f..255f,
                colors = SliderDefaults.colors(
                    thumbColor = getWholeColor(),
                    activeTrackColor = getWholeColor()
                )
            )
            Slider(
                value = blueColor.toFloat(),
                onValueChange = { updateColor('b', it) },
                valueRange = 0f..255f,
                colors = SliderDefaults.colors(
                    thumbColor = getWholeColor(),
                    activeTrackColor = getWholeColor()
                )
            )
        }
        Spacer(Modifier.height(10.dp))
        // Buttons (save & delete)
        LazyVerticalGrid(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            columns = GridCells.Fixed(if (editedEventType != null) 2 else 1)
        ) {
            // Delete button
            if (editedEventType != null) item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .fillMaxWidth()
                        .height(45.dp)
                        .combinedClickable(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.long_click_delete_info),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onLongClick = {
                                onDelete()
                            }
                        ),
                    shape = ButtonDefaults.shape,
                    colors = CardDefaults.cardColors(
                        containerColor = itemColor,
                        contentColor = ButtonDefaults.buttonColors().contentColor
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(Icons.Default.Delete, "delete")
                    }
                }
            }
            // Save button
            item {
                Button(
                    onClick = {
                        textError = false
                        if (textValue.isEmpty()) {
                            textError = true
                        } else {
                            onSave(
                                EventType(
                                    id = editedEventType?.id ?: -1,
                                    color = getWholeColor().toArgb(),
                                    text = textValue
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .fillMaxWidth()
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = itemColor)
                ) {
                    Icon(Icons.Default.Done, "save")
                }
            }
        }
    }
}