package com.lestec.eventify.ui

import android.text.format.DateFormat
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lestec.eventify.data.Boundaries
import com.lestec.eventify.data.EventEntry
import com.lestec.eventify.data.EventType
import com.lestec.eventify.data.LocalRepo
import com.lestec.eventify.ui.calendar.DayObj
import com.lestec.eventify.ui.calendar.MonthObj
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(private val repo: LocalRepo): ViewModel() {
    class Factory(private val localRepo: LocalRepo): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(localRepo) as T
    }

    // CardItems
    var eventTypes by mutableStateOf(listOf<EventType>())
        private set
    fun updateEventTypes() {
        eventTypes = repo.getEventsTypes()
    }

    var editBottomSheetOpen by mutableStateOf(false)
        private set
    fun updateEditBottomSheetOpen(value: Boolean) {
        editBottomSheetOpen = value
    }

    fun createEventType(color: Int, text: String) {
        repo.addEvent(EventType(-1, color, text))
    }

    fun createEventEntry(eventType: EventType) {
        repo.addEvent(EventEntry(-1, eventType.id, System.currentTimeMillis(), 0, ""))
    }

    init {
        updateEventTypes()
    }

    // Calendar
    val today: Calendar = Calendar.getInstance()
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
            // Set month name
            nameOfMonth = DateFormat.format(
                if ((calendar[Calendar.YEAR] == today[Calendar.YEAR])) "LLLL" else "LLLL yyyy",
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
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH))
        c.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY))
        val monthStart = c.timeInMillis
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
        c.set(Calendar.HOUR_OF_DAY, c.getActualMaximum(Calendar.HOUR_OF_DAY))
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
                if (c[Calendar.DAY_OF_MONTH] == calEditDay[Calendar.DAY_OF_MONTH]) {
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

    init {
        get3MonthsData(today, null)
    }

    // Show the day dialog
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
        // Init some data for dialog
        if (dayObj != null) {
            dataForDay = dayObj.listOfStats
            timeForDay = dayObj.timeMills
        }
    }
}