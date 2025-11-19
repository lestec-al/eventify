package com.lestec.eventify.data

data class Boundaries(
    val start: Long,
    val end: Long
)

data class EventType(
    val id: Int,
    val color: Int,
    val text: String
)

data class EventEntry(
    val id: Int,
    val typeId: Int,
    val date: Long,
    val color: Int,
    val text: String
)

const val types = "eventsTypes"
const val entries = "eventsEntries"
const val id = "id"
const val color = "color"
const val text = "text"
const val typeId = "typeId"
const val date = "date"
const val type = "type"