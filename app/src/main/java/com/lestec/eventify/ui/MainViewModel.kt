package com.lestec.eventify.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lestec.eventify.R
import com.lestec.eventify.data.Boundaries
import com.lestec.eventify.data.EventEntry
import com.lestec.eventify.data.EventType
import com.lestec.eventify.data.LocalRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.Calendar
import java.util.Objects

class MainViewModel(private val repo: LocalRepo): ViewModel() {
    class Factory(private val localRepo: LocalRepo): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(localRepo) as T
    }

    fun getScope() = viewModelScope

    var eventTypes by mutableStateOf(listOf<EventType>())
        private set
    private fun updateEventTypes() {
        eventTypes = repo.getEventsTypes()
    }

    var editSheetOpen by mutableStateOf(false)
        private set
    var editedEventType by mutableStateOf<EventType?>(null)
        private set
    fun updateEditSheetOpen(
        sheetOpen: Boolean,
        eventType: EventType? = null
    ) {
        editSheetOpen = sheetOpen
        editedEventType = eventType
    }

    fun createEventType(newEventType: EventType) {
        repo.addEvent(newEventType)
        updateEventTypes()
        updateEditSheetOpen(false)
    }

    fun updateEventType(eventType: EventType) {
        repo.updateEvent(eventType)
        updateEventTypes()
        updateEditSheetOpen(false)
        get3MonthsData(editedCalendar, null)
    }

    fun deleteEventType(eventType: EventType) {
        repo.deleteEvent(eventType)
        updateEventTypes()
        updateEditSheetOpen(false)
        get3MonthsData(editedCalendar, null)
    }

    fun createEventEntry(eventType: EventType) {
        repo.addEvent(EventEntry(-1, eventType.id, daySheetDate.timeInMillis, 0, ""))
        get3MonthsData(today, null)
    }

    private val today: Calendar = Calendar.getInstance()
    private var editedCalendar: Calendar = Calendar.getInstance()
    var currentPage = 1

    val weekNames = updateWeekNames()
    private fun updateWeekNames(): List<String> {
        val list = mutableListOf<String>()
        val c = Calendar.getInstance()
        var d = c.firstDayOfWeek
        c[Calendar.DAY_OF_WEEK] = d
        for (unused in 1..7) {
            list.add(DateFormat.format("EEE", c).toString())
            d = if ((d + 1 <= 7)) d + 1 else 1
            c[Calendar.DAY_OF_WEEK] = d
        }
        return list
    }

    var isDatePickerON by mutableStateOf(false)
        private set
    fun setIsDatePickerON(value: Boolean) {
        isDatePickerON = value
    }

    var nameOfMonth by mutableStateOf("")
        private set
    var monthsData by mutableStateOf(listOf<MonthObj>())
        private set
    /**
     * Get data for [calendar] & one month before & one month after
     */
    fun get3MonthsData(
        calendar: Calendar,
        pagerState: PagerState?
    ) {
        viewModelScope.launch {
            editedCalendar = calendar
            // Set month name
            nameOfMonth = DateFormat.format(
                if (calendar[Calendar.YEAR] == today[Calendar.YEAR]) "LLLL" else "LLLL yyyy",
                calendar
            ).toString()
            // Edit calendar
            val calEdit = Calendar.getInstance()
            calEdit.time = calendar.time
            calEdit.set(Calendar.DAY_OF_MONTH, 1)
            calEdit.set(Calendar.HOUR_OF_DAY, 0)
            // Set to -2 to make possible increasing +1 each time in for loop
            calEdit.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 2)
            // Loop for months
            val listOfMonths = mutableListOf<MonthObj>()
            for (m in 1..3) {
                calEdit.set(Calendar.MONTH, calEdit.get(Calendar.MONTH) + 1)
                listOfMonths.add(get1MonthData(calEdit))
            }
            monthsData = listOfMonths.toList()
            // Scroll to pos 1 (middle target pos)
            // Without that there are scroll bugs
            if (pagerState != null) {
                while (pagerState.isScrollInProgress) {
                    delay(100)
                }
                pagerState.scrollToPage(1)
            }
        }
    }

    fun addMonthToData(
        lastScrolledBackward: Boolean,
        landedCal: Calendar
    ) {
        // Set month name
        nameOfMonth = DateFormat.format(
            if ((landedCal[Calendar.YEAR] == today[Calendar.YEAR])) "LLLL" else "LLLL yyyy",
            landedCal
        ).toString()

        val landedCalEdit = Calendar.getInstance()
        landedCalEdit.timeInMillis = landedCal.timeInMillis
        landedCalEdit.set(Calendar.DAY_OF_MONTH, 1)
        landedCalEdit.set(Calendar.HOUR_OF_DAY, 0)
        if (lastScrolledBackward) {
            landedCalEdit.set(Calendar.MONTH, landedCalEdit.get(Calendar.MONTH) - 1)
        } else {
            landedCalEdit.set(Calendar.MONTH, landedCalEdit.get(Calendar.MONTH) + 1)
        }

        val calEdit = if (lastScrolledBackward) {
            val calEdit = Calendar.getInstance()
            calEdit.timeInMillis = monthsData[0].calendar.timeInMillis
            calEdit.set(Calendar.DAY_OF_MONTH, 1)
            calEdit.set(Calendar.HOUR_OF_DAY, 0)
            calEdit.set(Calendar.MONTH, calEdit.get(Calendar.MONTH) - 1)
            calEdit
        } else {
            val calEdit = Calendar.getInstance()
            calEdit.timeInMillis = monthsData.last().calendar.timeInMillis
            calEdit.set(Calendar.DAY_OF_MONTH, 1)
            calEdit.set(Calendar.HOUR_OF_DAY, 0)
            calEdit.set(Calendar.MONTH, calEdit.get(Calendar.MONTH) + 1)
            calEdit
        }

        val yearEquals = landedCalEdit.get(Calendar.YEAR) == calEdit.get(Calendar.YEAR)
        val monthEquals = landedCalEdit.get(Calendar.MONTH) == calEdit.get(Calendar.MONTH)
        if (yearEquals && monthEquals) {
            val newM = get1MonthData(calEdit)
            val list = if (lastScrolledBackward) {
                listOf(newM) + monthsData
            } else {
                monthsData + listOf(newM)
            }
            monthsData = list.toList()
        }
    }

    private fun get1MonthData(calEdit: Calendar): MonthObj {
        // Calc the remaining days for the previous month
        var day = 1
        var calendarWeekDay = calEdit.firstDayOfWeek
        val firstMonthWeekDay = calEdit[Calendar.DAY_OF_WEEK]
        if (calendarWeekDay != firstMonthWeekDay) {
            var i = 1
            while (i < 8) {
                calendarWeekDay += 1
                if (calendarWeekDay > 7) calendarWeekDay = 1
                if (calendarWeekDay == firstMonthWeekDay) {
                    day -= i
                    break
                }
                i += 1
            }
        }
        // Get data for 1 month
        val c = Calendar.getInstance()
        c.timeInMillis = calEdit.timeInMillis
        c[Calendar.DAY_OF_MONTH] = day
        c[Calendar.HOUR_OF_DAY] = c.getActualMinimum(Calendar.HOUR_OF_DAY)
        val monthStart = c.timeInMillis
        c.timeInMillis = calEdit.timeInMillis
        c[Calendar.DAY_OF_MONTH] = day + 41
        c[Calendar.HOUR_OF_DAY] = c.getActualMaximum(Calendar.HOUR_OF_DAY)
        val monthEnd = c.timeInMillis
        val dataForMonth = repo.getEventsEntries(Boundaries(monthStart, monthEnd))
        // Setup days
        val listOfDays = mutableListOf<DayObj>()
        for (pos in 0..41) { // day..(day+41) ???
            // This calendar represent date of the specific day
            // And at the start it often be for previous month
            val calEditDay = Calendar.getInstance()
            calEditDay[Calendar.YEAR] = calEdit[Calendar.YEAR]
            calEditDay[Calendar.MONTH] = calEdit[Calendar.MONTH]
            calEditDay[Calendar.DAY_OF_MONTH] = day // Day may be negative
            // Get data for this day
            val dataThisDay = mutableListOf<EventEntry>()
            for (i in dataForMonth) {
                c.timeInMillis = i.date
                // If day from position == day from data
                if (
                    c[Calendar.DAY_OF_MONTH] == calEditDay[Calendar.DAY_OF_MONTH] &&
                    c[Calendar.MONTH] == calEditDay[Calendar.MONTH]
                ) {
                    dataThisDay.add(i)
                }
            }
            dataThisDay.sortBy { it.date }
            listOfDays.add(
                DayObj(
                    dayNumber = calEditDay[Calendar.DAY_OF_MONTH].toString(),
                    isThisMonth = calEditDay[Calendar.MONTH] == calEdit[Calendar.MONTH],
                    isToday = (
                            today[Calendar.YEAR] == calEditDay[Calendar.YEAR] &&
                                    today[Calendar.MONTH] == calEditDay[Calendar.MONTH] &&
                                    today[Calendar.DAY_OF_MONTH] == calEditDay[Calendar.DAY_OF_MONTH]
                            ),
                    timeMills = calEditDay.timeInMillis,
                    listOfStats = dataThisDay
                )
            )
            day++
        }
        val staticCal = Calendar.getInstance()
        staticCal.timeInMillis = calEdit.timeInMillis
        return MonthObj(days = listOfDays, calendar = staticCal)
    }

    var isShowDayDialog by mutableStateOf(false)
        private set
    var timeForDay by mutableLongStateOf(0L)
        private set
    var dataForDay by mutableStateOf(listOf<EventEntry>())
        private set
    fun setIsShowDayDialog(
        value: Boolean,
        dayObj: DayObj? = null
    ) {
        isShowDayDialog = value
        if (dayObj != null) {
            dataForDay = dayObj.listOfStats
            timeForDay = dayObj.timeMills
            val tempC = Calendar.getInstance()
            tempC.timeInMillis = dayObj.timeMills
            daySheetDate = tempC
        }
    }

    fun deleteEventEntry(eventEntry: EventEntry) {
        repo.deleteEvent(eventEntry)
        dataForDay = dataForDay.filter { eventEntry.id != it.id }
        get3MonthsData(editedCalendar, null)
    }

    var cardItemsOpen by mutableStateOf(false)
        private set
    fun updateCardItemsOpen(value: Boolean) {
        cardItemsOpen = value
    }

    var daySheetDate by mutableStateOf(today)
        private set
    @OptIn(ExperimentalMaterial3Api::class)
    fun updateCardItemsDateTime(selectedTime: TimePickerState?) {
        if (selectedTime != null) {
            daySheetDate.set(Calendar.HOUR_OF_DAY, selectedTime.hour)
            daySheetDate.set(Calendar.MINUTE, selectedTime.minute)
        } else {
            daySheetDate = today
        }
    }

    init {
        updateEventTypes()
        get3MonthsData(today, null)
    }


    // SETTINGS
    val settings = listOf(
        SettingsObj(text = R.string.import_db, icon = Icons.Default.FileDownload),
        SettingsObj(text = R.string.export_db, icon = Icons.Default.FileUpload)
    )

    fun getAppVersion(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${context.getString(R.string.app_ver)} ${pInfo.versionName}"
        } catch (_: Exception) { "" }
    }

    var isAskDialogOpen by mutableStateOf(false)
        private set
    var askDialogAction: Int? by mutableStateOf(null)
        private set
    fun setAskDialog(
        visibility: Boolean,
        actionStringId: Int?
    ) {
        isAskDialogOpen = visibility
        askDialogAction = actionStringId
    }

    fun importDB(
        launcher: ManagedActivityResultLauncher<Intent, androidx.activity.result.ActivityResult>
    ) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("application/json")
        launcher.launch(intent)
    }

    fun resultImportDB(
        context: Context,
        result: androidx.activity.result.ActivityResult
    ) {
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(
                    Objects.requireNonNull<Uri?>(result.data!!.data)
                )
                val inReader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
                var inputLine: String?
                val response = StringBuilder()
                while ((inReader.readLine().also { inputLine = it }) != null) {
                    response.append(inputLine)
                }
                inReader.close()
                inputStream?.close()
                if (!repo.import(response.toString())) {
                    throw java.lang.Exception("Import error")
                }
                Toast.makeText(context, R.string.ok, Toast.LENGTH_LONG).show()
            } catch (e: java.lang.Exception) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show()
            }
            viewModelScope.launch {
                delay(1000)
                updateEventTypes()
                get3MonthsData(today, null)
            }
        }
    }

    fun exportDB(
        launcher: ManagedActivityResultLauncher<Intent, androidx.activity.result.ActivityResult>
    ) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("application/json")
        intent.putExtra(Intent.EXTRA_TITLE, "dataEventify.json")
        launcher.launch(intent)
    }

    fun resultExportDB(
        context: Context,
        result: androidx.activity.result.ActivityResult
    ) {
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            try {
                val os: OutputStream? = context.contentResolver.openOutputStream(Objects.requireNonNull<Uri>(result.data!!.data))
                val input: ByteArray = repo.export().toString().toByteArray(StandardCharsets.UTF_8)
                checkNotNull(os)
                os.write(input, 0, input.size)
                os.close()
                Toast.makeText(context, R.string.ok, Toast.LENGTH_LONG).show()
            } catch (e: java.lang.Exception) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show()
            }
        }
    }
}