package com.funnco.scheduler.domain.interactor

import android.util.Log
import com.funnco.scheduler.data.repository.ApiService
import com.funnco.scheduler.data.repository.model.EventModel
import com.funnco.scheduler.data.repository.model.TemplateEventModel
import com.funnco.scheduler.data.repository.model.UserModel
import com.funnco.scheduler.domain.model.BusynessType
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetTime
import java.util.Comparator
import java.util.Objects
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

object DataInteractor {

    val COOLDOWN_TIME = TimeUnit.MINUTES.toMillis(15);

    var allUsers: List<UserModel> = emptyList()
    var schedule: List<EventModel> = emptyList()
    var templates: List<TemplateEventModel> = emptyList()
    lateinit var currentUser: UserModel

    init {
        ApiService.getAllUsers {result ->
            allUsers = result
        }
    }

    fun subscribeForUser(currentUser: UserModel){
        ApiService.subscribeToScheduleUpdates(currentUser) {
            schedule = it
        }
        ApiService.subscribeToTemplateUpdates(currentUser) {
            templates = it
        }
        this.currentUser = currentUser
    }

    fun getScheduleForDate(date: LocalDate): List<EventModel> {
        return schedule.filter { event -> event.date!!.isEqual(date) }
    }

    fun getScheduleForWeek(weekOffset: Int): List<EventModel> {
        val now = LocalDate.now()
        val currentWeekStartDate = now.minusDays(now.dayOfWeek.value.toLong())
        val expectedWeekStartDate = currentWeekStartDate.plusWeeks(weekOffset.toLong())
        return schedule.stream()
            .filter { event -> event.date!!.isAfter(expectedWeekStartDate)}
            .filter { event -> event.date!!.isBefore(expectedWeekStartDate.plusDays(8)) }
            .collect(Collectors.toList())
    }

    fun getUserBusyness(): BusynessType {
        val currentSchedule = getScheduleForDate(LocalDate.now())
        val currentEvent = currentSchedule.stream()
            .filter { event -> event.startTime!!.isAfter(OffsetTime.now()) }
            .filter { event -> event.endTime!!.isBefore(OffsetTime.now()) }
            .findFirst().orElse(null)

        // Сейчас пользователь занят
        if (Objects.nonNull(currentEvent)) {
            return BusynessType.BUSY
        }

        // COOLDDOWN_TIME перед следующим событием
        var minutesBeforeNextEvent = currentSchedule.stream()
            .filter { event -> event.endTime!!.isAfter(event.endTime) } // Убрать уже завершенные события
            .sorted(Comparator.comparing(EventModel::startTime))
            .findFirst()
            .map { event -> Duration.between(OffsetTime.now(), event.startTime).toMinutes() }
            .orElse(null)
        if (minutesBeforeNextEvent != null && minutesBeforeNextEvent < COOLDOWN_TIME) {
            return BusynessType.PREPARATION
        }

        // COOLDDOWN_TIME после предыдущего события
        var minutesAfterLastEvent = currentSchedule.stream()
            .filter { event -> event.startTime!!.isBefore(event.startTime) } // Убрать еще не начавшиеся события
            .sorted(Comparator.comparing(EventModel::startTime))
            .findFirst()
            .map { event -> Duration.between(OffsetTime.now(), event.endTime).toMinutes() }
            .orElse(null)
        if (minutesAfterLastEvent != null && minutesAfterLastEvent < COOLDOWN_TIME) {
            return BusynessType.FINISHING
        }

        return BusynessType.FREE
    }

    fun pushTemplate(template: TemplateEventModel, callback: () -> Unit) {
        ApiService.pushTemplate(template, currentUser, callback)
    }

    fun deleteTemplate(id: String, callback: () -> Unit) {
        ApiService.deleteTemplate(id, callback)
    }
}