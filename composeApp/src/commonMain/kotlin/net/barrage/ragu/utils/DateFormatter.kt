package net.barrage.ragu.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Formats an ISO date (represented as an Instant) into a human-readable string.
 *
 * This function converts the given Instant to a LocalDateTime using the current system's
 * time zone, then formats it into a string in the format "Month DD, YYYY".
 *
 * @param instant The Instant to be formatted.
 * @return A string representation of the date in the format "Month DD, YYYY".
 *         For example, "January 01, 2023".
 */
fun formatIsoDateToReadable(instant: Instant): String {
    // Convert the Instant to LocalDateTime using the current system's time zone
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    // Format the month name: lowercase, then capitalize the first letter
    val month = localDateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }

    // Format the day with leading zero if necessary
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')

    // Get the year as a string
    val year = localDateTime.year.toString()

    // Combine the formatted parts into the final string
    return "$month $day, $year"
}