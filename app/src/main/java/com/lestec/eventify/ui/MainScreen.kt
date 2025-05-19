package com.lestec.eventify.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lestec.eventify.ui.calendar.CalendarCard
import com.lestec.eventify.ui.cards.CardItems

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MainScreen(
    onBack: () -> Unit,
    vm: MainViewModel
) {
    BackHandler(onBack = onBack)
    val calendarMaxGridHeight = LocalConfiguration.current.screenHeightDp
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            viewModelFactory {  }
            CalendarCard(
                gridHeightDp = calendarMaxGridHeight / 2,
                modifier = Modifier.padding(horizontal = 4.dp),
                vm = vm
            )
            Spacer(Modifier.height(8.dp))
            CardItems(
                modifier = Modifier.padding(horizontal = 4.dp),
                vm = vm
            )
        }
    }
}