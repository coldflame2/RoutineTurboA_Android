package com.app.routineturboa.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

object TimeUtils {

    private val formatter = DateTimeFormatter.ofPattern("hh:mm a, MM-dd-yyyy", Locale.US)

    /**
     * String (hh:mm a) to LocalDateTime
     * A default date is added to input string
     */
    fun strToDateTime(timeString: String): LocalDateTime {
        val stringPatten = "hh:mm a"
        val date = LocalDate.of(2024, 7, 15)
        val timeFormatter = DateTimeFormatter.ofPattern(stringPatten)
        val localTime = LocalTime.parse(timeString, timeFormatter)
        return LocalDateTime.of(date, localTime)
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
