package com.funnco.scheduler.domain.interactor

import android.util.Log
import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.data.model.UserModel

import com.funnco.scheduler.data.repository.UserRepository
import com.funnco.scheduler.data.retrofit.dto.ScheduleDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.stream.Collectors

object UserDataInteractor {

    private var mapOfUsersSchedules: Map<Int, List<ScheduleModel>> = emptyMap()
    var listOfUsers: List<UserModel> = emptyList()

    init {
        subscribeToScheduleUpdate {
            mapOfUsersSchedules = it
        }
    }

    fun getAllDaysSchedules(userModel: UserModel): List<ScheduleModel> {
        if (userModel.id == -1 || mapOfUsersSchedules[userModel.id]?.isEmpty() != false) {
            return emptyList()
        }
        return mapOfUsersSchedules[userModel.id]!!.sortedBy { it.startTime }
    }

    fun postAndUpdateSchedules(updatedSchedules: List<ScheduleModel>, callback: (Boolean) -> Unit) {
        UserRepository.postSchedules(updatedSchedules.reversed().distinctBy { it.startTime }.distinctBy { it.endTime }.reversed().stream().map { entry -> ScheduleDTO(entry) }
            .collect(Collectors.toList())) {
                callback(it)
        }
    }

    fun getCurrentDaySchedules(userModel: UserModel): List<ScheduleModel> {
        if (userModel.id == -1) {
            return emptyList()
        }
        return mapOfUsersSchedules[userModel.id]!!.filter { it.dayNumber == LocalDate.now().dayOfWeek.value }
            .sortedBy { it.startTime }
    }

    private var previousBusinessJob: Job? = null

    fun isUserFree(userModel: UserModel, callback: (Boolean) -> Unit) {
        if (previousBusinessJob != null) {
            Log.i("TAGG", "Canceled job")
            previousBusinessJob!!.cancel()
        }
        previousBusinessJob = GlobalScope.launch {
            Log.i("TAGG", "Started job for user ${userModel.id}")
            while (true) {
                if (userModel.id == -1) {
                    callback(true)
                }

                val currentTime = LocalTime.now()
                val currentDayOfWeek = LocalDate.now().dayOfWeek.value

                if (mapOfUsersSchedules[userModel.id]?.filter { entry -> entry.dayNumber == currentDayOfWeek }?.size == 0) {
                    callback(true)
                }

                mapOfUsersSchedules[userModel.id]?.forEach { entry ->
                    if (entry.dayNumber == currentDayOfWeek) {
                        callback(
                            currentTime.compareTo(entry.startTime) * currentTime.compareTo(
                                entry.endTime
                            ) > 0
                        )
                    }
                }
                delay(10000)
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
                    delay(10000)
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
                        listOfUsers = it
                    }
                }
                delay(10000)
            }
        }
    }
}