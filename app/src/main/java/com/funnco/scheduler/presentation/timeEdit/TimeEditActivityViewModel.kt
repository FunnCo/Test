package com.funnco.scheduler.presentation.timeEdit

import androidx.lifecycle.ViewModel
import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.domain.interactor.UserDataInteractor
import kotlinx.coroutines.flow.MutableStateFlow

class TimeEditActivityViewModel : ViewModel() {
    private val userDataInteractor = UserDataInteractor
    var userSchedules = MutableStateFlow(emptyList<ScheduleModel>())
    var newEntries = mutableListOf<ScheduleModel>()
    fun postNewSchedules(callback: (isSuccessful: Boolean) -> Unit){
        userDataInteractor.postAndUpdateSchedules(newEntries) {
            callback(it)
        }
    }

    fun getUserSchedules(userModel: UserModel) {
        userSchedules.value = userDataInteractor.getAllDaysSchedules(userModel)
    }

    fun getCurrentUser(userId: Int): UserModel {
        return userDataInteractor.listOfUsers.find { it.id == userId }!!
    }

}