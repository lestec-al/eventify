package com.lestec.eventify.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.lestec.eventify.ui.sheets.CardItemsSheet
import com.lestec.eventify.ui.sheets.DaySheet
import com.lestec.eventify.ui.sheets.EditSheet
import com.lestec.eventify.ui.sheets.DatePickerSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    onSettings: () -> Unit,
    vm: MainViewModel
) {
    BackHandler(onBack = onBack)
    val localDensity = LocalDensity.current

    var screenHeightDp by remember { mutableStateOf(0.dp) }
    val itemHeightDp = screenHeightDp / 6

    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { vm.monthsData.size }
    )
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            launch {
                vm.currentPage = page
                vm.addMonthToData(
                    pagerState.lastScrolledBackward,
                    vm.monthsData[page].calendar
                )
            }
        }
    }

    // For icon button long click
    val interactionSource = remember { MutableInteractionSource() }
    val viewConfiguration = LocalViewConfiguration.current
    LaunchedEffect(interactionSource) {
        var isLongClick = false
        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    isLongClick = false
                    delay(viewConfiguration.longPressTimeoutMillis)
                    isLongClick = true
                    vm.get3MonthsData(Calendar.getInstance(), pagerState)
                }
                is PressInteraction.Release -> {
                    if (!isLongClick) {
                        vm.setIsDatePickerON(true)
                    }
                }
            }
        }
    }

    // Dialogs
    DatePickerSheet(
        visible = vm.isDatePickerON,
        initMonth = vm.monthsData[vm.currentPage].calendar.get(Calendar.MONTH),
        initYear = vm.monthsData[vm.currentPage].calendar.get(Calendar.YEAR),
        onConfirm = { month, year ->
            val c = Calendar.getInstance()
            c.set(Calendar.MONTH, month)
            c.set(Calendar.YEAR, year)
            c.set(Calendar.DAY_OF_MONTH, 1)
            vm.get3MonthsData(c, pagerState)
            vm.setIsDatePickerON(false)
        },
        onCancel = {
            vm.setIsDatePickerON(false)
        },
        onReset = {
            vm.get3MonthsData(Calendar.getInstance(), pagerState)
            vm.setIsDatePickerON(false)
        }
    )
    DaySheet(vm = vm)
    EditSheet(vm = vm)
    CardItemsSheet(vm = vm)

    // Main view
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = vm.nameOfMonth.upperFirstChar())
                },
                actions = {
                    // Set date button
                    IconButton(
                        onClick = {},
                        interactionSource = interactionSource
                    ) {
                        Icon(Icons.Default.EditCalendar, null)
                    }
                    // Settings button
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, null)
                    }
                }
            )
        },
        floatingActionButton = {
            // Add card button
            FloatingActionButton(
                onClick = {
                    vm.updateCardItemsDateTime(null)
                    vm.updateCardItemsOpen(true)
                }
            ) {
                Icon(Icons.Default.Add, null)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        // Show months to all mounts
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding),
            key = { vm.monthsData[it].calendar.timeInMillis }
        ) { page ->
            val month = vm.monthsData[page]
            Column {
                // Row with day names
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    userScrollEnabled = false,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items = vm.weekNames) {
                        Box(
                            modifier = Modifier.height(25.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = it)
                        }
                    }
                }
                // Days
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    userScrollEnabled = false,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { layoutSize ->
                            with(localDensity) {
                                screenHeightDp = layoutSize.height.toDp()
                            }
                        }
                ) {
                    items(items = month.days) {
                        Box(
                            modifier = Modifier
                                .height(itemHeightDp)
                                .border(width = 0.3.dp, color = DividerDefaults.color)
                                .clickable {
                                    vm.setIsShowDayDialog(true, it)
                                },
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Day number
                                Text(
                                    text = it.dayNumber,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .fillMaxWidth()
                                        .background(
                                            color = if (it.isToday) {
                                                MaterialTheme.colorScheme.onBackground
                                            } else {
                                                Color.Transparent
                                            },
                                            shape = CardDefaults.shape
                                        ),
                                    textAlign = TextAlign.Center,
                                    color = if (it.isToday) {
                                        if (isSystemInDarkTheme()) Color.Black else Color.White
                                    } else {
                                        if (it.isThisMonth) {
                                            Color.Unspecified
                                        } else {
                                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                        }
                                    }
                                )
                                // Stats for the specific day
                                it.listOfStats.forEach { it1 ->
                                    val text = it1.text
                                    val height = TextUnit(10f, TextUnitType.Sp)
                                    Text(
                                        text = if ((text.length > 4)) text.substring(0, 4) else text,
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp, vertical = 0.5.dp)
                                            .fillMaxWidth()
                                            .background(
                                                color = Color(it1.color),
                                                shape = RoundedCornerShape(4.dp)
                                            ),
                                        textAlign = TextAlign.Center,
                                        color = if (it1.color == Color.White.toArgb()) Color.Black else Color.White,
                                        fontSize = height,
                                        lineHeight = height,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}