package com.funnco.scheduler.presentation.timeEdit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.domain.interactor.UserDataInteractor
import kotlinx.coroutines.flow.MutableStateFlow

class TimeEditActivityViewModel : ViewModel() {
    private val userDataInteractor = UserDataInteractor
    var userSchedules = MutableStateFlow(emptyList<ScheduleModel>())
    var newEntries = mutableListOf<ScheduleModel>()

    var isDialogNeededNow = mutableStateOf(false)

    var deletedEntries = mutableListOf<ScheduleModel>()
    fun postNewSchedules(callback: (isSuccessful: Boolean) -> Unit){
        userDataInteractor.postAndUpdateSchedules(newEntries) {
            callback(it)
        }

        if(deletedEntries.isNotEmpty()){
            userDataInteractor.removeEntries(deletedEntries)
        }
    }

    fun getUserSchedules(userModel: UserModel) {
        userSchedules.value = userDataInteractor.getAllDaysSchedules(userModel)
    }

    fun getCurrentUser(userId: Int): UserModel {
        return userDataInteractor.listOfUsers.find { it.id == userId }!!
    }

    fun deleteSchedule(scheduleId: Int){
        var wasScheduleNew = newEntries.remove(newEntries.find { it.id == scheduleId })
        if(!wasScheduleNew){
            var entryToDelete = userSchedules.value.find { it.id == scheduleId }!!

            deletedEntries.add(entryToDelete)
            var newUserSchedules = userSchedules.value.toMutableList()
            newUserSchedules.remove(entryToDelete)
            userSchedules.value = newUserSchedules
        }
    }

}