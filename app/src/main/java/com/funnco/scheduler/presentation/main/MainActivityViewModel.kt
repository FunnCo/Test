package com.funnco.scheduler.presentation.main

import androidx.lifecycle.ViewModel
import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.domain.interactor.UserDataInteractor
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivityViewModel: ViewModel() {

    val userDataInteractor = UserDataInteractor()
    val listOfUsers = MutableStateFlow(emptyList<UserModel>())
    val mapOfUserSchedules = MutableStateFlow(emptyMap<Int, List<ScheduleModel>>())
    var isCurrentUserFree = MutableStateFlow(true)

    fun subscribeToUserBusiness(userModel: UserModel){
        userDataInteractor.isUserFree(userModel) {
            isCurrentUserFree.value = it
        }
    }


    init {
        userDataInteractor.subscribeToScheduleUpdate() {
            mapOfUserSchedules.value = it
            userDataInteractor.subscribeToUserUpdate() {
                listOfUsers.value = it
            }
        }
    }

}