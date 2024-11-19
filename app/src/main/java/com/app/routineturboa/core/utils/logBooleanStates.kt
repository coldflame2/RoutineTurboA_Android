package com.app.routineturboa.core.utils

import android.util.Log
import androidx.compose.runtime.Composable

fun logBooleanStates(tag: String, booleanStates: Map<String, Boolean>) {
    booleanStates.forEach { (name, value) ->
        Log.d(tag, "$name: $value")
    }
}
