package com.app.routineturboa.core.dbutils

import com.app.routineturboa.ui.theme.LocalCustomColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.app.routineturboa.ui.theme.CustomColorsPalette

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

    // Helper function to get color for a task type with scaling
    fun utilsGetColor(
        customColors: CustomColorsPalette,
        taskType: String?,
        intensity: Float = 1f
    ): Color {
        val baseColor = when (taskType) {
            MAIN -> customColors.mainTaskColor
            HELPER -> customColors.helperTaskColor
            QUICK -> customColors.quickTaskColor
            BASICS -> customColors.basicsTaskColor
            UNDEFINED -> customColors.undefinedTaskColor
            else -> Color.Gray
        }
        return baseColor.scaleColor(intensity)
    }

    // Extension function to scale a color by a given factor
    private fun Color.scaleColor(scaleFactor: Float): Color {
        return this.copy(
            red = (this.red * scaleFactor).coerceIn(0f, 1f),
            green = (this.green * scaleFactor).coerceIn(0f, 1f),
            blue = (this.blue * scaleFactor).coerceIn(0f, 1f)
        )
    }
}
