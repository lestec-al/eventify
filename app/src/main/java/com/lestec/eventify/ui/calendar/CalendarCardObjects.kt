package com.lestec.eventify.ui.calendar

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