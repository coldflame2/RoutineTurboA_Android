package com.app.routineturboa.utils

import java.text.SimpleDateFormat
import java.util.Locale

object TimeUtils {
    fun convertTo12HourFormat(time: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(time)
        return outputFormat.format(date!!)
    }
}