package com.app.routineturboa.utils

import android.util.Log
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object Converters {
    const val tag = "Converters"

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE  // 2011-12-03'
    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME  // 10:15:30

    private val possibleUiTimeFormatters = listOf(
        DateTimeFormatter.ofPattern("h:mm a", Locale.US),
        DateTimeFormatter.ofPattern("hh:mm a", Locale.US),
        DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.US),
        DateTimeFormatter.ofPattern("H:mm", Locale.US),
        DateTimeFormatter.ofPattern("HH:mm", Locale.US)
    )

    @TypeConverter
    @JvmStatic
    fun dateToString(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    @JvmStatic
    fun stringToDate(dateString: String?): LocalDate? {
        return dateString?.let {
            LocalDate.parse(it, dateFormatter)
        }
    }

    @TypeConverter
    @JvmStatic
    fun timeToString(time: LocalTime?): String? {
        return time?.format(timeFormatter)
    }

    @TypeConverter
    @JvmStatic
    fun stringToTime(timeString: String?): LocalTime? {
        return timeString?.let {
            LocalTime.parse(it, timeFormatter)
        }
    }

    // ------------------- Additional Utility converter functions -----------------

    // Convert a UI-friendly time string (e.g., "09:00 AM") to LocalTime
    fun uiStringToTime(uiTimeString: String?): LocalTime? {
        uiTimeString?.let { timeString ->
            possibleUiTimeFormatters.forEach { uiTimeFormatter ->
                try {
                    return LocalTime.parse(timeString, uiTimeFormatter)
                } catch (e: Exception) {
                    // Ignore parsing exceptions and continue with the next format
                }
            }
        }
        return null  // if uiTimeString is null
    }
}
