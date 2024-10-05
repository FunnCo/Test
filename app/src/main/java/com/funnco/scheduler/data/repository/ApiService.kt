package com.funnco.scheduler.data.repository

import ApiServiceBuilder
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.funnco.scheduler.data.repository.api.ScheduleAPI
import com.funnco.scheduler.data.repository.api.UserAPI
import com.funnco.scheduler.data.repository.model.EventModel
import com.funnco.scheduler.data.repository.model.NoteModel
import com.funnco.scheduler.data.repository.model.TemplateEventModel
import com.funnco.scheduler.data.repository.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.example.common.restutils.RepetitiveRequestManager
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import kotlin.streams.toList

object ApiService {

    val REQUEST_FREQUENCY = TimeUnit.SECONDS.toMillis(3)
    val repetitiveManager = RepetitiveRequestManager()

    val userApi = ApiServiceBuilder(UserAPI::class.java).build()
    val scheduleApi = ApiServiceBuilder(ScheduleAPI::class.java).build()

    fun deleteSingleEvent(eventId: String, callback: () -> Unit) {
        MainScope().launch(Dispatchers.IO) {
            scheduleApi.deleteSingleEvent(eventId)
            callback()
        }
    }

    fun pushNote(note: NoteModel, eventId: String,  callback: () -> Unit) {
        MainScope().launch(Dispatchers.IO) {
            scheduleApi.upsertNote(note, eventId)
            callback()
        }
    }

    fun deleteNote(noteId: String,  callback: () -> Unit) {
        MainScope().launch(Dispatchers.IO) {
            scheduleApi.deleteNote(noteId)
            callback()
        }
    }

    fun getAllUsers(callback: (users: List<UserModel>) -> Unit) {
        MainScope().launch(Dispatchers.IO) {
            callback(userApi.getAllUsers())
        }
    }

    fun pushTemplate(template: TemplateEventModel, user: UserModel, callback: () -> Unit) {
        MainScope().launch(Dispatchers.IO) {
            scheduleApi.upsertTemplate(template, user.id)
            callback()
        }
    }



    fun subscribeToScheduleUpdates(user: UserModel, callback: (result: List<EventModel>) -> Unit) {
        repetitiveManager.stopRepetitive("schedule_update")
        repetitiveManager.executeRepetitive(
            methodName = "schedule_update",
            method = {
                callback(scheduleApi.getUserSchedule(user.id))
            },
            repeatInterval = REQUEST_FREQUENCY
        )
    }

    fun subscribeToTemplateUpdates(
        user: UserModel,
        callback: (result: List<TemplateEventModel>) -> Unit
    ) {
        repetitiveManager.stopRepetitive("schedule_update_template")
        repetitiveManager.executeRepetitive(
            methodName = "schedule_update_template",
            method = {
                callback(scheduleApi.getUserScheduleTemplate(user.id))
            },
            repeatInterval = REQUEST_FREQUENCY
        )
    }

    fun deleteTemplate(id: String, callback: () -> Unit) {
        MainScope().launch(Dispatchers.IO) {
            scheduleApi.deleteTemplate(id)
            callback()
        }
    }



}