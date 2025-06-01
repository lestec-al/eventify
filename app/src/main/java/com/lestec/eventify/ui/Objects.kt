package com.lestec.eventify.ui

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.lestec.eventify.data.EventEntry
import java.util.Calendar

data class DayObj(
    val dayNumber: String,
    val isThisMonth: Boolean,
    val isToday: Boolean,
    val timeMills: Long,
    val listOfStats: List<EventEntry>
)

data class MonthObj(
    val days: List<DayObj>,
    val calendar: Calendar
)

data class SettingsObj(
    @StringRes val text: Int,
    val icon: ImageVector
)

enum class Screens { Calendar, Settings }