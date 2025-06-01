package com.lestec.eventify.ui

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import java.util.Date

fun String.upperFirstChar() = this.replaceFirstChar { i ->
    if (i.isLowerCase()) i.uppercase() else i.toString()
}

fun Date.formatDate(context: Context): String = DateFormat
    .getMediumDateFormat(context)
    .format(this)

fun Long.formatMillsDate(context: Context): String = DateFormat
    .getMediumDateFormat(context)
    .format(this)

fun Long.formatMillsTime(context: Context): String = DateFormat
    .getTimeFormat(context)
    .format(this)

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }