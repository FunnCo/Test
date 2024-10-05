package com.funnco.scheduler.data.repository.api

import com.funnco.scheduler.data.repository.model.EventModel
import com.funnco.scheduler.data.repository.model.NoteModel
import com.funnco.scheduler.data.repository.model.TemplateEventModel
import org.example.common.restutils.Body
import org.example.common.restutils.DELETE
import org.example.common.restutils.GET
import org.example.common.restutils.Header
import org.example.common.restutils.POST
import org.example.common.restutils.Param
import org.example.common.restutils.RestClient

@RestClient(baseUrl = "http://188.120.240.201:8080/api")
//@RestClient(baseUrl = "http://192.168.31.15:8080/api")
interface ScheduleAPI {

    @GET("schedule")
    fun getUserSchedule(@Param("userId") userId: String): List<EventModel>

    @GET("schedule/template")
    fun getUserScheduleTemplate(@Param("userId") userId: String): List<TemplateEventModel>

    @POST("schedule")
    fun upsertTemplate(
        @Body templateEventModel: TemplateEventModel,
        @Param("userId") userId: String,
        @Header("Content-type") type: String = "application/json"
    ): Any

    @DELETE("schedule")
    fun deleteTemplate(
        @Param("templateId") templateId: String
    ): Any

    @POST("schedule/note")
    fun upsertNote(
        @Body noteModel: NoteModel,
        @Param("eventId") eventId: String,
        @Header("Content-type") type: String = "application/json"
    ): Any

    @DELETE("schedule/note")
    fun deleteNote(
        @Param("noteId") noteId: String,
        @Header("Content-type") type: String = "application/json"
    ): Any

    @DELETE("schedule/single")
    fun deleteSingleEvent(
        @Param("eventId") noteId: String,
        @Header("Content-type") type: String = "application/json"
    ): Any
}