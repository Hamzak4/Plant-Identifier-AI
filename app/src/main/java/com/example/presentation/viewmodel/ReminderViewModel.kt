package com.example.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.local.ReminderEntity
import com.example.data.repository.PlantRepository
import com.example.di.ServiceLocator
import com.example.utils.ReminderWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ReminderViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: PlantRepository = ServiceLocator.getRepository(application)
) : AndroidViewModel(application) {

    val reminders: StateFlow<List<ReminderEntity>> = repository.getAllReminders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addReminder(plantName: String, type: String, intervalDays: Int) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val nextDueDate = now + TimeUnit.DAYS.toMillis(intervalDays.toLong())
            val entity = ReminderEntity(
                plantName = plantName,
                type = type,
                intervalDays = intervalDays,
                lastDoneDate = now,
                nextDueDate = nextDueDate,
                isEnabled = true
            )
            repository.insertReminder(entity)
            scheduleWorkManagerReminder(entity)
        }
    }

    fun completeReminderTask(reminder: ReminderEntity) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val nextDueDate = now + TimeUnit.DAYS.toMillis(reminder.intervalDays.toLong())
            val updated = reminder.copy(
                lastDoneDate = now,
                nextDueDate = nextDueDate
            )
            repository.insertReminder(updated)
            scheduleWorkManagerReminder(updated)
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }

    fun toggleReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            val updated = reminder.copy(isEnabled = !reminder.isEnabled)
            repository.insertReminder(updated)
            if (updated.isEnabled) {
                scheduleWorkManagerReminder(updated)
            }
        }
    }

    private fun scheduleWorkManagerReminder(reminder: ReminderEntity) {
        try {
            val context = getApplication<Application>()
            val workManager = WorkManager.getInstance(context)

            val data = Data.Builder()
                .putString("plant_name", reminder.plantName)
                .putString("care_type", reminder.type)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(reminder.intervalDays.toLong(), TimeUnit.DAYS)
                .setInputData(data)
                .build()

            workManager.enqueue(workRequest)
        } catch (e: Exception) {
            android.util.Log.e("ReminderViewModel", "WorkManager scheduling failed: ${e.message}")
        }
    }
}
