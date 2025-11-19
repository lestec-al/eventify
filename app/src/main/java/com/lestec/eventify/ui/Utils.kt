package com.lestec.eventify.ui

import android.content.Context
import android.text.format.DateFormat

fun String.upperFirstChar() = this.replaceFirstChar { i ->
    if (i.isLowerCase()) i.uppercase() else i.toString()
}

fun Long.formatMillsDate(context: Context): String = DateFormat
    .getMediumDateFormat(context)
    .format(this)

fun Long.formatMillsTime(context: Context): String = DateFormat
    .getTimeFormat(context)
    .format(this)