package com.lestec.eventify.ui.sheets

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lestec.eventify.ui.components.BaseSheet
import com.lestec.eventify.ui.upperFirstChar
import java.util.Calendar

@Composable
fun DatePickerSheet(
    visible: Boolean,
    initMonth: Int,
    initYear: Int,
    onConfirm: (month: Int, year: Int) -> Unit,
    onCancel: () -> Unit,
    onReset: () -> Unit
) {
    if (visible) {
        val months by remember {
            // Get all months (to viewModel ?)
            val c = Calendar.getInstance()
            c[Calendar.DAY_OF_MONTH] = 1
            val max = c.getActualMaximum(Calendar.MONTH)
            val months = mutableListOf<String>()
            var i = 0
            while (i <= max) {
                c[Calendar.MONTH] = i
                months.add(i, DateFormat.format("LLL", c).toString())
                i += 1
            }
            mutableStateOf(months.toList())
        }
        var month by remember { mutableStateOf(months[initMonth]) }
        var year by remember { mutableIntStateOf(initYear) }

        BaseSheet(
            onDismiss = onCancel,
            upActions = {
                // Year & buttons
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onReset) {
                        Icon(Icons.Outlined.Refresh, "reset")
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { year-- }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null)
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = year.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { year++ }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { onConfirm(months.indexOf(month), year) }) {
                        Icon(Icons.Outlined.Check, "ok")
                    }
                }
            }
        ) {
            // Months
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                items(items = months) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (month == it) MaterialTheme.colorScheme.primary else Color.Unspecified
                            )
                            .clickable { month = it },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it.upperFirstChar(),
                            modifier = Modifier.padding(8.dp),
                            color = if (month == it) MaterialTheme.colorScheme.background else Color.Unspecified,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}