package com.funnco.scheduler.domain.interactor

import com.funnco.scheduler.data.repository.ApiService
import com.funnco.scheduler.data.repository.model.EventModel
import com.funnco.scheduler.data.repository.model.NoteModel
import com.funnco.scheduler.data.repository.model.TemplateEventModel
import com.funnco.scheduler.data.repository.model.UserModel
import com.funnco.scheduler.domain.model.BusynessType
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetTime
import java.time.ZoneOffset
import java.util.Comparator
import java.util.Objects
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import kotlin.math.abs


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

    val systemOffset = ZoneOffset.systemDefault().rules.getOffset(Instant.now())

    fun handleTimeZones(initTime: OffsetTime): OffsetTime{
        return initTime.withOffsetSameInstant(systemOffset)
    }

    fun handleIncomingEvents(newEvents: List<EventModel>){
        val undeletableEvents = schedule.filter { event -> event.notes?.any { note -> !note.canBeDeleted } ?: false }.toList()
        val filteredNewEvents = newEvents.filter { event -> undeletableEvents.all { it.id != event.id } }.toMutableList()
        filteredNewEvents.addAll(undeletableEvents)
        schedule = filteredNewEvents.stream()
            .peek { event ->
                event.startTime = handleTimeZones(event.startTime!!)
                event.endTime = handleTimeZones(event.endTime!!)
            }
            .sorted(Comparator.comparing(EventModel::startTime)).collect(Collectors.toList())

//        schedule = it.stream()
//            .peek { event ->
//                event.startTime = handleTimeZones(event.startTime!!)
//                event.endTime = handleTimeZones(event.endTime!!)
//            }
//            .sorted(Comparator.comparing(EventModel::startTime))
//            .collect(Collectors.toList())
    }

    fun handleIncomingTemplates(newEvents: List<TemplateEventModel>){
        templates = newEvents.stream()
            .peek { event ->
                event.startTime = handleTimeZones(event.startTime!!)
                event.endTime = handleTimeZones(event.endTime!!)
            }
            .sorted(Comparator.comparing(TemplateEventModel::startTime))
            .collect(Collectors.toList())
    }

    fun subscribeForUser(currentUser: UserModel){
        ApiService.subscribeToScheduleUpdates(currentUser) {
            handleIncomingEvents(it)
        }
        ApiService.subscribeToTemplateUpdates(currentUser) {
            handleIncomingTemplates(it)
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
        val now = OffsetTime.now()
        val currentEvent = currentSchedule.stream()
            .filter { event -> event.startTime!!.isBefore(now) }
            .filter { event -> event.endTime!!.isAfter(now) }
            .findFirst().orElse(null)

        // Сейчас пользователь занят
        if (Objects.nonNull(currentEvent)) {
            return BusynessType.BUSY
        }

        // COOLDDOWN_TIME перед следующим событием
        var minutesBeforeNextEvent = currentSchedule.stream()
            .filter { event -> event.endTime!!.isAfter(now) } // Убрать уже завершенные события
            .sorted(Comparator.comparing(EventModel::startTime))
            .findFirst()
            .map { event -> Duration.between(now, event.startTime).toMillis() }
            .orElse(null)
        if (minutesBeforeNextEvent != null && abs(minutesBeforeNextEvent) < COOLDOWN_TIME) {
            return BusynessType.PREPARATION
        }

        // COOLDDOWN_TIME после предыдущего события
        var minutesAfterLastEvent = currentSchedule.stream()
            .filter { event -> event.startTime!!.isBefore(now) } // Убрать еще не начавшиеся события
            .sorted(Comparator.comparing(EventModel::startTime).reversed())
            .findFirst()
            .map { event -> Duration.between(now, event.endTime).toMillis() }
            .orElse(null)
        if (minutesAfterLastEvent != null && abs(minutesAfterLastEvent) < COOLDOWN_TIME) {
            return BusynessType.FINISHING
        }

        return BusynessType.FREE
    }

    fun pushEvent(event: EventModel, callback: () -> Unit) {
        ApiService.pushEvent(event, currentUser, callback)
    }

    fun pushTemplate(template: TemplateEventModel, callback: () -> Unit) {
        ApiService.pushTemplate(template, currentUser, callback)
    }

    fun deleteTemplate(id: String, callback: () -> Unit) {
        ApiService.deleteTemplate(id, callback)
    }

    fun pushNote(note: NoteModel, eventId: String, callback: () -> Unit) {
        if(note.flag == false && note.content.isNullOrBlank() && note.id != null){
            ApiService.deleteNote(note.id!!, callback)
        } else {
            ApiService.pushEvent(note, eventId, callback)
        }
    }

    fun deleteSingleEvent(eventModel: EventModel, callback: () -> Unit){
        ApiService.deleteSingleEvent(eventModel.id!!, callback)
    }
}