package com.funnco.scheduler.presentation.main

import androidx.lifecycle.ViewModel
import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.domain.interactor.UserDataInteractor
import com.funnco.scheduler.domain.model.BusynessType
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivityViewModel : ViewModel() {

    private val userDataInteractor = UserDataInteractor
    val listOfUsers = MutableStateFlow(emptyList<UserModel>())
    var isCurrentUserFree = MutableStateFlow(BusynessType.FREE)
    var isUpdateAllowed = true
    var currentSchedule = MutableStateFlow(emptyList<ScheduleModel>())


    fun getUserBusyness(userModel: UserModel) {
        userDataInteractor.isUserFree(userModel){
            if (isUpdateAllowed) {
                isCurrentUserFree.value = it
                currentSchedule.value = userDataInteractor.getCurrentDaySchedules(userModel)
            }
        }
    }

    init {
        userDataInteractor.subscribeToUserUpdate() {
            if (isUpdateAllowed) {
                listOfUsers.value = it
            }
        }
    }

}