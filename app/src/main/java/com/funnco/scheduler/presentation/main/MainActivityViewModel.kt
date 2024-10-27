package com.funnco.scheduler.presentation.main

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funnco.scheduler.data.repository.model.EventModel
import com.funnco.scheduler.data.repository.model.UserModel
import com.funnco.scheduler.domain.interactor.DataInteractor
import com.funnco.scheduler.domain.model.BusynessType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class MainActivityViewModel : ViewModel() {

    private val dataInteractor = DataInteractor
    val listOfUsers = MutableStateFlow(emptyList<UserModel>())
    var isCurrentUserFree = MutableStateFlow(BusynessType.FREE)
    var isUpdateAllowed = true

    var jobsList: MutableMap<Int, Job> = mutableMapOf()
    var daysSchedule : MutableMap<Int, MutableStateFlow<List<EventModel>>> = mutableMapOf()

    fun subscribeForSchedule(dayOffset: Int) {
        jobsList[dayOffset] = viewModelScope.launch(Dispatchers.IO) {
            while (isActive){
                if(isUpdateAllowed) {
                    val selectedDate = LocalDate.now().plusDays(dayOffset.toLong())
                    if (!daysSchedule.containsKey(dayOffset)) {
                        daysSchedule[dayOffset] = MutableStateFlow(emptyList())
                    }
                    daysSchedule[dayOffset]?.value = dataInteractor.getScheduleForDate(selectedDate)
                }
                TimeUnit.MICROSECONDS.sleep(50)
            }
        }
    }

    fun getUserBusyness() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                if (isUpdateAllowed) {
                    isCurrentUserFree.value = dataInteractor.getUserBusyness()
                }
                TimeUnit.MICROSECONDS.sleep(50)
            }
        }
    }

    fun updateCurrentUser(userModel: UserModel) {
        dataInteractor.subscribeForUser(userModel)
        jobsList.forEach { it.value.cancel() }
    }

    init {
        this.viewModelScope.launch(Dispatchers.IO) {
            while (listOfUsers.value.isEmpty()) {
                listOfUsers.value = dataInteractor.allUsers
                TimeUnit.MICROSECONDS.sleep(50)
            }
        }
    }
}