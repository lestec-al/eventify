package com.lestec.eventify.ui

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.ui.graphics.vector.ImageVector
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

data class Setting(
    val text: Int,
    val icon: ImageVector,
    val action: (
        context: Context,
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
    ) -> Unit
)

enum class Screen { Calendar, Settings }

enum class CreatedType { Type, Entry }