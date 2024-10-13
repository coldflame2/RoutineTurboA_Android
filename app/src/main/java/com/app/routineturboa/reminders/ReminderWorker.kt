//package com.app.routineturboa.reminders
//
//import android.content.Context
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import com.app.routineturboa.data.local.TaskEntity
//
//class ReminderWorker(
//    context: Context,
//    params: WorkerParameters
//) : CoroutineWorker(context, params) {
//
//    override suspend fun doWork(): Result {
//        val taskId = inputData.getInt("TASK_ID", -1)
//        if (taskId != -1) {
//            val dataRepository = getDataRepository(context)
//            val task = dataRepository.getTaskById(taskId)
//            task?.let {
//                showNotification(it)
//            }
//        }
//        return Result.success()
//    }
//
//    private fun showNotification(task: TaskEntity) {
//        // Build and display the notification
//    }
//}
