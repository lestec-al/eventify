package com.lestec.eventify.ui.calendar

import android.text.format.DateFormat
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lestec.eventify.ui.MainViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayBottomSheet(
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
                //vm.dataForDay.forEach {}
                //if (vm.dataForDay.isEmpty()) {}
            }
        }
    }
}