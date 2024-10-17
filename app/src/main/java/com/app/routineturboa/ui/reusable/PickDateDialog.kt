package com.app.routineturboa.ui.reusable

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickDateDialog(
    isShowPickDateDialog: MutableState<Boolean>,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    val tag = "PickDateDialog"

    if (isShowPickDateDialog.value) {
        Log.d(tag, "PickDateDialog starts...")
        val initialDateMillis = selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateMillis
        )
        DatePickerDialog(
            onDismissRequest = { isShowPickDateDialog.value = false },
            confirmButton = {
                ElevatedButton(
                    onClick = {
                        Log.d(tag, "clicked on DatePicker")
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            Log.d(tag, "$millis")
                            val datePicked = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateChange(datePicked)
                        } else {
                            Log.d(tag, "null.")
                        }
                        isShowPickDateDialog.value = false
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(width = 100.dp, height = 48.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 8.dp,
                        disabledElevation = 0.dp
                    )
                ) {
                    Text("OK",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            dismissButton = {
                ElevatedButton(onClick = { isShowPickDateDialog.value = false },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(width = 100.dp, height = 48.dp)) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f))
                }
            },
            properties = DialogProperties(
                dismissOnClickOutside = true,
                dismissOnBackPress = true,
                usePlatformDefaultWidth = false
            ),
        ) {
            DatePicker(
                state = datePickerState,
            )
        }
    }
}


