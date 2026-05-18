package com.lestec.eventify.ui

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
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
    val icon: ImageVector,
    val action: (
        context: Context,
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
    ) -> Unit
)

enum class Screens { Calendar, Settings }

enum class CreatedType { Type, Entry }