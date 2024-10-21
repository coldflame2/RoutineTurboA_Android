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

    fun strToDateTimeWithDate(date: LocalDate, timeString: String): LocalDateTime {
        // Define the formatter for parsing the time string
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

        // Parse the time string to a LocalTime
        val time = LocalTime.parse(timeString, timeFormatter)

        // Combine the given date with the parsed time to create a LocalDateTime
        return LocalDateTime.of(date, time)
    }

    fun strToDateTime(
        inputString: String,
        date: LocalDate? = LocalDate.now(),
        defaultDateTime: LocalDateTime = LocalDateTime.now() // Default value if parsing fails
    ): LocalDateTime {
        val inputStringTrimmed = inputString.trim().uppercase()

        for (eachFormat in possibleFormats) {
            try {
                val inputLocalTime = LocalTime.parse(inputStringTrimmed, eachFormat)
                return LocalDateTime.of(date, inputLocalTime)
            } catch (e: DateTimeParseException) {
                Log.e("TimeUtils", "Error parsing time string: $inputString using format: $eachFormat")
            }
        }

        // If all parsing attempts fail, return the specified default LocalDateTime
        Log.w("TimeUtils", "Parsing Failed. Returning default LocalDateTime: $defaultDateTime")
        return defaultDateTime
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
