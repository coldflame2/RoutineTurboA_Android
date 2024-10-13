package com.app.routineturboa.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

// Utility function to emit the current LocalDateTime every minute
fun dateTimeEmitterFlow(seconds: Int = 60): Flow<LocalDateTime> = flow {
    val milliseconds = seconds * 1000L
    while (true) {
        emit(LocalDateTime.now())
        delay(milliseconds)
    }
}