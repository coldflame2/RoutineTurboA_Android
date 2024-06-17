package com.app.routineturboa.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TimeUtils {

    private val inputFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputFormat: SimpleDateFormat
        get() = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val timeOnlyFormat: SimpleDateFormat
        get() = SimpleDateFormat("HH:mm:ss", Locale.getDefault())  // Ensure the time format includes seconds

    fun convertTo12HourFormat(time: String): String {
        val date = inputFormat.parse(time)
        return outputFormat.format(date!!)
    }

    fun convertTo24HourFormat(time: String): String {
        val date = outputFormat.parse(time)
        return timeOnlyFormat.format(date!!)
    }

    fun formatToDatabaseTime(date: String, time: String): String {
        val datetime = "$date $time:00"  // Append seconds to match "HH:mm:ss"
        val dateObj = inputFormat.parse(datetime)
        return inputFormat.format(dateObj!!)
    }

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
