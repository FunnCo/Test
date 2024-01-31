package com.funnco.scheduler.presentation.main

import androidx.lifecycle.ViewModel
import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.domain.interactor.UserDataInteractor
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivityViewModel : ViewModel() {

    private val userDataInteractor = UserDataInteractor
    val listOfUsers = MutableStateFlow(emptyList<UserModel>())
    var isCurrentUserFree = MutableStateFlow(true)
    var isUpdateAllowed = true
    var currentSchedule = MutableStateFlow(emptyList<ScheduleModel>())


    fun subscribeToUserBusiness(userModel: UserModel) {
        userDataInteractor.isUserFree(userModel) {
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