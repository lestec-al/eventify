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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lestec.eventify.R
import com.lestec.eventify.ui.calendar.CalendarCard
import com.lestec.eventify.ui.cards.CardItems

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MainScreen(
    onBack: () -> Unit,
    vm: MainViewModel
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val calendarMaxGridHeight = LocalConfiguration.current.screenHeightDp
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = context.getString(R.string.app_name)) },
                actions = {
                    // Add card button
                    IconButton(onClick = { vm.updateEditSheetOpen(true) }) {
                        Icon(Icons.Default.AddBox, null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
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