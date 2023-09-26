package com.funnco.scheduler.domain.interactor

import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.data.model.UserModel

import com.funnco.scheduler.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class UserDataInteractor {

    private var mapOfUsersSchedules: Map<Int, List<ScheduleModel>> = emptyMap()
    fun isUserFree(userModel: UserModel, callback: (Boolean) -> Unit) {
        GlobalScope.launch {
            while (true) {
                if (userModel.id == -1) {
                    callback(true)
                }
                val currentTime = LocalTime.now()
                val currentDayOfWeek = LocalDate.now().dayOfWeek.value
                mapOfUsersSchedules[userModel.id]?.forEach { entry ->
                    if (entry.dayNumber == currentDayOfWeek) {
                        callback(
                            currentTime.compareTo(entry.startTime) * currentTime.compareTo(
                                entry.endTime
                            ) > 0
                        )
                    }
                }
                delay(15000)
            }
        }
    }


    fun subscribeToScheduleUpdate(callback: (Map<Int, List<ScheduleModel>>) -> Unit) {
        GlobalScope.launch {
            var previousSyncPassed = true
            while (true) {
                if (previousSyncPassed) {
                    previousSyncPassed = false
                    UserRepository.getAllSchedules {
                        val requiredMap = mutableMapOf<Int, List<ScheduleModel>>()
                        it.stream().forEach { entry ->
                            if (!requiredMap.containsKey(entry.userId)) {
                                requiredMap[entry.userId] = mutableListOf()
                            }
                            (requiredMap[entry.userId] as MutableList).add(entry)
                        }
                        mapOfUsersSchedules = requiredMap
                        callback(requiredMap)
                        previousSyncPassed = true
                    }
                    delay(15000)
                }
            }
        }
    }

    fun subscribeToUserUpdate(callback: (List<UserModel>) -> Unit) {
        GlobalScope.launch {
            var previousSyncPassed = true
            while (true) {
                if (previousSyncPassed) {
                    previousSyncPassed = false
                    UserRepository.getAllUsers {
                        callback(it)
                        previousSyncPassed = true
                    }
                }
                delay(15000)
            }
        }
    }
}