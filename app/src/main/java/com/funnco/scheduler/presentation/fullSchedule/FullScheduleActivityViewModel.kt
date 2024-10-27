package com.funnco.scheduler.presentation.fullSchedule

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funnco.scheduler.data.repository.model.EventModel
import com.funnco.scheduler.data.repository.model.UserModel
import com.funnco.scheduler.domain.interactor.DataInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class FullScheduleActivityViewModel : ViewModel() {
    private val dataInteractor = DataInteractor

    var jobsList: MutableMap<Int, Job> = mutableMapOf()
    var weeksSchedule: MutableMap<Int, MutableStateFlow<List<EventModel>>> = mutableMapOf()

    fun getCurrentUser(userId: String): UserModel {
        return dataInteractor.allUsers.find { it.id == userId }!!
    }

    init {
        for(weekOffset in -4..4){
            if(jobsList.containsKey(weekOffset)){
                jobsList[weekOffset]!!.cancel()
            }
            jobsList[weekOffset] = viewModelScope.launch(Dispatchers.IO) {
                while (isActive) {
                    if (!weeksSchedule.containsKey(weekOffset)) {
                        weeksSchedule[weekOffset] = MutableStateFlow(emptyList())
                    }
                    weeksSchedule[weekOffset]?.update { dataInteractor.getScheduleForWeek(weekOffset) }
                    TimeUnit.MICROSECONDS.sleep(50)
                }
            }
        }
    }

    fun parseWeekScheduleToMap(models: List<EventModel>): Map<Int, List<EventModel>> {
        val resultMap: MutableMap<Int, MutableList<EventModel>> = mutableMapOf()
        models.forEach { model ->
            if(!resultMap.containsKey(model.date!!.dayOfWeek.value)){
                resultMap[model.date!!.dayOfWeek.value] = mutableListOf()
            }
            resultMap[model.date!!.dayOfWeek.value]!!.add(model)
        }
        return resultMap
    }

}