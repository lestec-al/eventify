package com.lestec.eventify.ui.calendar

import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lestec.eventify.R
import com.lestec.eventify.ui.EmptyBox
import com.lestec.eventify.ui.MainViewModel
import com.lestec.eventify.ui.formatMillsTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaySheet(
    onDismissRequest: () -> Unit,
    visible: Boolean,
    vm: MainViewModel
) {
    if (visible) {
        val context = LocalContext.current
        ModalBottomSheet(onDismissRequest = onDismissRequest) {
            // Date
            Text(
                text = DateFormat.getLongDateFormat(context).format(Date(vm.timeForDay)),
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            // Stats
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors()
            ) {
                vm.dataForDay.forEach {
                    val color = Color(it.color)
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .height(60.dp)
                            .fillMaxWidth()
                            .clip(CardDefaults.shape)
                            .combinedClickable(
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.long_click_delete_info),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onLongClick = {
                                    vm.deleteEventEntry(it)
                                }
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = it.text, color = color)
                        Text(text = it.date.formatMillsTime(context), color = color)
                    }
                    HorizontalDivider()
                }
                if (vm.dataForDay.isEmpty()) {
                    EmptyBox()
                }
            }
        }
    }
}