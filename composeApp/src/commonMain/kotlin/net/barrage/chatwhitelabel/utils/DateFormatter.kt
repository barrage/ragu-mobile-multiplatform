package net.barrage.chatwhitelabel.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun timestampToHhMm(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.hour.toString().padStart(2, '0') +
        ":" +
        localDateTime.minute.toString().padStart(2, '0')
}
