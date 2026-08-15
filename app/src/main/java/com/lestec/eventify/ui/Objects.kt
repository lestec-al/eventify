package com.lestec.eventify.ui

import com.lestec.eventify.data.EventEntry
import java.util.Calendar

data class Day(
    val dayNumber: String,
    val isThisMonth: Boolean,
    val isToday: Boolean,
    val timeMills: Long,
    val listOfStats: List<EventEntry>
)

data class Month(
    val days: List<Day>,
    val calendar: Calendar
)

enum class Screen { Calendar, Settings }

enum class CreatedType { Type, Entry }