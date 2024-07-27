package com.app.routineturboa.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Locale

object TimeUtils {

    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ROOT)
    val possibleFormats = listOf(
        DateTimeFormatter.ofPattern("h:mm a", Locale.US),
        DateTimeFormatter.ofPattern("hh:mm a", Locale.US),
        DateTimeFormatter.ofPattern("H:mm", Locale.US),
        DateTimeFormatter.ofPattern("HH:mm", Locale.US)
    )

    /**
     * String (various time formats) to LocalDateTime
     * A default date is added to input string
     */
    fun strToDateTime(timeString: String): LocalDateTime {
        val trimmedTimeString = timeString.trim().uppercase()

        for (formatter in possibleFormats) {
            try {
                val localTime = LocalTime.parse(trimmedTimeString, formatter)
                return LocalDateTime.of(LocalDate.now(), localTime)
            } catch (e: DateTimeParseException) {
                Log.e("TimeUtils", "Error parsing time string: $timeString")
            }
        }

        // If we've exhausted all formatters without success, throw an exception
        throw IllegalArgumentException("Error parsing time string: $timeString")
    }

    fun dateTimeToString(dateTime: LocalDateTime): String {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        return dateTimeFormatter.format(dateTime)
    }

    private val inputFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputFormat: SimpleDateFormat
        get() = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val timeOnlyFormat: SimpleDateFormat
        get() = SimpleDateFormat("HH:mm:ss", Locale.getDefault())  // Ensure the time format includes seconds

    fun isValidTimeFormat(time: String): Boolean {
        return try {
            outputFormat.parse(time)
            true
        } catch (e: ParseException) {
            false
        }
    }

    fun addDurationToTime(startTime: String, duration: Int): String {
        val calendar = Calendar.getInstance()
        val date = try {
            inputFormat.parse(startTime)
        } catch (e: ParseException) {
            outputFormat.parse(startTime)
        }
        calendar.time = date!!
        calendar.add(Calendar.MINUTE, duration)
        return outputFormat.format(calendar.time)
    }

}
