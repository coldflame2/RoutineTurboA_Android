package com.app.routineturboa.utils

object TaskTypes {
    const val MAIN = "main"
    const val HELPER = "helper"
    const val QUICK = "quick"
    const val UNDEFINED = "undefined"
    const val BASICS = "basics"

    // Function to return all task types as a list
    fun getAllTaskTypes(): List<String> {
        return listOf(MAIN, HELPER, QUICK, BASICS, UNDEFINED)
    }
}
