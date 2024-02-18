package com.funnco.scheduler.domain.interactor

import android.util.Log
import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.data.model.UserModel

import com.funnco.scheduler.data.repository.SupabaseRepository
import com.funnco.scheduler.domain.model.BusynessType
import com.funnco.scheduler.domain.model.NoteModel
import com.funnco.scheduler.domain.model.SimpleScheduleModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import java.util.stream.Collectors

object UserDataInteractor {

    const val REQUEST_DELAY = 2000L

    private var mapOfUsersSchedules: Map<Int, List<ScheduleModel>> = emptyMap()
    var listOfUsers: List<UserModel> = emptyList()

    init {
        subscribeToScheduleUpdate {
            mapOfUsersSchedules = it
        }
    }

    fun removeEntries(entriesToDelete: List<ScheduleModel>) {
        GlobalScope.launch {
            SupabaseRepository.deleteSchedule(entriesToDelete)
        }
    }

    fun insertOrChangeTempNote(tempNoteModel: NoteModel, callback: (Boolean, NoteModel) -> Unit) {
        GlobalScope.launch {
            SupabaseRepository.upsertTempNote(tempNoteModel, callback)
        }
    }

    fun getAllDaysSchedules(userModel: UserModel): List<ScheduleModel> {
        if (userModel.id == -1 || mapOfUsersSchedules[userModel.id]?.isEmpty() != false) {
            return emptyList()
        }
        return mapOfUsersSchedules[userModel.id]!!.sortedBy { it.startTime }
    }

    fun postAndUpdateSchedules(updatedSchedules: List<ScheduleModel>, callback: (Boolean) -> Unit) {
        GlobalScope.launch {
            SupabaseRepository.upsertSchedules(updatedSchedules.reversed()
                .distinctBy { it.startTime.toSecondOfDay() }
                .distinctBy { it.endTime.toSecondOfDay() }
                .reversed()
                .stream()
                .map { SimpleScheduleModel(it) }
                .collect(Collectors.toList())
            ) {
                callback(it)
            }
        }
    }

    fun getDaySchedules(userModel: UserModel, dayOffset: Int = 0): List<ScheduleModel> {
        if (userModel.id == -1) {
            return emptyList()
        }
        var selectedDayNumber = LocalDate.now().dayOfWeek.value
        if (selectedDayNumber + dayOffset > 7) {
            selectedDayNumber = (selectedDayNumber + dayOffset) % 7
        } else {
            if (selectedDayNumber + dayOffset < 1) {
                selectedDayNumber = 7 - (selectedDayNumber + dayOffset)
            } else {
                selectedDayNumber += dayOffset
            }
        }

        Log.d("TEST", "$selectedDayNumber")
        return try {
            mapOfUsersSchedules[userModel.id]!!
                .filter { it.dayNumber == selectedDayNumber }
                .sortedBy { it.startTime }
                .filter { schedule ->
                    schedule.tempNotes?.stream()
                        ?.filter {
                            it.note
                                .toLowerCase(Locale.ROOT)
                                .contains("отмен")
                                    && it.dateOfNote!!.isEqual(LocalDate.now().plusDays(dayOffset.toLong()))

                        }
                        ?.collect(Collectors.toList())?.isEmpty() ?: true
                }
        } catch (exception: NullPointerException) {
            emptyList()
        }
    }

    private var previousBusinessJob: Job? = null
    private var subscribedUser: UserModel? = null

    private fun processBusyness(userTodaySchedule: List<ScheduleModel>?): BusynessType {
        val currentTime = LocalTime.now()

        if (userTodaySchedule?.isNullOrEmpty() == false) {
            for (entry in userTodaySchedule) {

                var startTimeToCurrentDiff =
                    currentTime.toSecondOfDay() - entry.startTime.toSecondOfDay()
                var endTimeToCurrentDiff =
                    currentTime.toSecondOfDay() - entry.endTime.toSecondOfDay()

                if (startTimeToCurrentDiff > -900 && startTimeToCurrentDiff <= 0) {
                    return BusynessType.PREPARATION
                }
                if (endTimeToCurrentDiff < 900 && endTimeToCurrentDiff >= 0) {
                    return BusynessType.FINISHING
                }

                var result =
                    currentTime.compareTo(entry.startTime) * currentTime.compareTo(
                        entry.endTime
                    ) > 0
                if (!result) {
                    return BusynessType.BUSY
                }
            }
        }

        return BusynessType.FREE
    }

    fun isUserFree(userModel: UserModel, callback: (BusynessType) -> Unit) {
        if (previousBusinessJob != null) {
            Log.i("TAGG", "Canceled job")
            previousBusinessJob!!.cancel()
        }
        subscribedUser = userModel
        previousBusinessJob = GlobalScope.launch {
            Log.i("TAGG", "Started job for user ${userModel.id}")
            while (isActive) {
                if (subscribedUser?.id == -1) {
                    continue
                }
                val entriesToCheck = getDaySchedules(userModel)
                callback(processBusyness(entriesToCheck))
                delay(REQUEST_DELAY)
            }
        }
    }


    fun subscribeToScheduleUpdate(callback: (Map<Int, List<ScheduleModel>>) -> Unit) {
        GlobalScope.launch {
            var previousSyncPassed = true
            while (true) {
                if (previousSyncPassed) {
                    previousSyncPassed = false
                    SupabaseRepository.getAllSchedules {
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
                    delay(REQUEST_DELAY)
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
                    SupabaseRepository.getAllUsers {
                        callback(it)
                        previousSyncPassed = true
                        listOfUsers = it
                    }
                }
                delay(REQUEST_DELAY)
            }
        }
    }
}