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

    private val possibleFormats = listOf(
        DateTimeFormatter.ofPattern("h:mm a", Locale.US),
        DateTimeFormatter.ofPattern("hh:mm a", Locale.US),
        DateTimeFormatter.ofPattern("H:mm", Locale.US),
        DateTimeFormatter.ofPattern("HH:mm", Locale.US)
    )

    fun isoStrToDateTime(inputIsoStr: String): LocalDateTime {
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
        return LocalDateTime.parse(inputIsoStr, inputFormatter)
    }

    /**
     * String (various time formats) to LocalDateTime
     * A default date is added to input string
     */
    fun strToDateTime(inputString: String): LocalDateTime {
        val inputStringTrimmed = inputString.trim().uppercase()

        for (eachFormat in possibleFormats) {
            try {
                val inputLocalTime = LocalTime.parse(inputStringTrimmed, eachFormat)
                val inputLocalDateTime = LocalDateTime.of(LocalDate.now(), inputLocalTime)
                return inputLocalDateTime

            } catch (e: DateTimeParseException) {
                Log.e("TimeUtils", "Error parsing time string: $inputString")
            }
        }

        // If we've exhausted all formatters without success, throw an exception
        throw IllegalArgumentException("Error parsing time string: $inputString")
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
