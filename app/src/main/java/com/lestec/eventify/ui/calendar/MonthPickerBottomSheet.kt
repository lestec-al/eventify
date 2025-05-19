package com.lestec.eventify.ui.calendar

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lestec.eventify.ui.upperFirstChar
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthPickerBottomSheet(
    visible: Boolean,
    currentMonth: Int,
    currentYear: Int,
    confirmButtonCLicked: (month: Int, year: Int) -> Unit,
    cancelClicked: () -> Unit,
    resetClicked: () -> Unit,
    primaryColor: Color? = null
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
        var month by remember { mutableStateOf(months[currentMonth]) }
        var year by remember { mutableIntStateOf(currentYear) }
        val itemColor = primaryColor ?: MaterialTheme.colorScheme.primary

        ModalBottomSheet(onDismissRequest = cancelClicked) {
            // Years
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        year--
                    },
                    modifier = Modifier.rotate(90f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null)
                }
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = year.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        year++
                    },
                    modifier = Modifier.rotate(90f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                }
            }
            // Months
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                months.forEach { item {
                    val isThisMonthChosen = month == it
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isThisMonthChosen) itemColor else Color.Unspecified
                            )
                            .clickable {
                                month = it
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it.upperFirstChar(),
                            modifier = Modifier.padding(8.dp),
                            color = if (isThisMonthChosen) {
                                MaterialTheme.colorScheme.background
                            } else Color.Unspecified,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } }
            }
            // Buttons
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                columns = GridCells.Fixed(2)
            ) {
                items(count = 2) {
                    Button(
                        onClick = { if (it == 0) {
                            resetClicked()
                        } else {
                            confirmButtonCLicked(months.indexOf(month), year)
                        } },
                        modifier = Modifier.padding(horizontal = 5.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = itemColor)
                    ) {
                        Icon(
                            imageVector = if (it == 0) {
                                Icons.Default.Refresh
                            } else {
                                Icons.Default.Check
                            },
                            contentDescription = if (it == 0) "reset" else "ok"
                        )
                    }
                }
            }
        }
    }
}