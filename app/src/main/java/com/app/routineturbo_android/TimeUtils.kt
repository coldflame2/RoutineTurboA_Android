package com.app.routineturbo_android

import java.text.SimpleDateFormat
import java.util.Locale

fun convertTo12HourFormat(time: String): String {
    val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val date = inputFormat.parse(time)
    return outputFormat.format(date!!)
}
