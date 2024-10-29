package com.app.routineturboa.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.app.routineturboa.ui.theme.LocalCustomColors

@Composable
fun getTaskColor(taskType: String): Color {
    val customColors = LocalCustomColors.current
    return remember(taskType) { // Only recompute when `taskType` changes
        when (taskType) {
            TaskTypes.MAIN -> customColors.mainTaskColor
            TaskTypes.BASICS -> customColors.basicsTaskColor
            TaskTypes.HELPER -> customColors.helperTaskColor
            TaskTypes.QUICK -> customColors.quickTaskColor
            TaskTypes.UNDEFINED -> customColors.undefinedTaskColor
            else -> customColors.gray300
        }
    }
}
