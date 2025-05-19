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