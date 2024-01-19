package com.funnco.scheduler.presentation.fullSchedule

import androidx.lifecycle.ViewModel
import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.domain.interactor.UserDataInteractor
import kotlinx.coroutines.flow.MutableStateFlow

class FullScheduleActivityViewModel : ViewModel() {
    private val userDataInteractor = UserDataInteractor
    var userSchedules = MutableStateFlow(emptyList<ScheduleModel>())

    fun getCurrentUser(userId: Int): UserModel {
        return userDataInteractor.listOfUsers.find { it.id == userId }!!
    }

    fun getUserSchedules(userModel: UserModel) {
        userSchedules.value = userDataInteractor.getAllDaysSchedules(userModel)
    }

}