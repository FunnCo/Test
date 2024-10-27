package com.funnco.scheduler.presentation.timeEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funnco.scheduler.data.repository.model.EventModel
import com.funnco.scheduler.data.repository.model.TemplateEventModel
import com.funnco.scheduler.data.repository.model.UserModel
import com.funnco.scheduler.domain.interactor.DataInteractor
import com.funnco.scheduler.domain.model.WeekDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class TimeEditActivityViewModel : ViewModel() {
    private val dataInteractor = DataInteractor

    var jobsList: MutableMap<Int, Job> = mutableMapOf()
    var daysTemplates: MutableMap<Int, MutableStateFlow<List<TemplateEventModel>>> = mutableMapOf()

    init {
        for (dayOffset in 1..7) {
            jobsList[dayOffset] = viewModelScope.launch(Dispatchers.IO) {
                while (isActive) {
                    updateSchedule(dayOffset)
                    TimeUnit.MICROSECONDS.sleep(50)
                }
            }
        }
    }

    private fun updateSchedule(dayOffset: Int) {
        if (!daysTemplates.containsKey(dayOffset)) {
            daysTemplates[dayOffset] = MutableStateFlow(emptyList())
        }
        daysTemplates[dayOffset]?.update {
            dataInteractor.templates.filter { entry ->
                WeekDay.valueOf(
                    entry.day!!
                ).value == dayOffset
            }
        }
    }

    fun getCurrentUser(userId: String): UserModel {
        return dataInteractor.allUsers.find { it.id == userId }!!
    }

    fun createOrUpdateTemplate(template: TemplateEventModel, callback: () -> Unit) {
        dataInteractor.pushTemplate(template) {
            callback()
        }
    }

    fun deleteTemplate(id: String, callback: () -> Unit) {
        dataInteractor.deleteTemplate(id){
            callback()
        }
    }
}