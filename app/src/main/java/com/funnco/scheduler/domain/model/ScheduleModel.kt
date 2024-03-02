package com.funnco.scheduler.data.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.funnco.scheduler.domain.model.NoteModel
import com.funnco.scheduler.domain.model.SimpleScheduleModel
import java.time.LocalDate
import java.time.LocalTime

@JsonIgnoreProperties(value = [ "isRescheduledEntry", "rescheduledFrom", "rescheduledTo"])
data class ScheduleModel(
    @JsonProperty("id")
    val id: Int?,
    @JsonProperty("userId")
    val userId: Int,
    @JsonProperty("startTime")
    var startTime: LocalTime = LocalTime.now(),
    @JsonProperty("endTime")
    var endTime: LocalTime = LocalTime.now(),
    @JsonProperty("dayNumber")
    val dayNumber: Int,
    @JsonProperty("note")
    var note: String? = null,
    @JsonProperty("tempNotes")
    var tempNotes: List<NoteModel>? = null,
    @JsonProperty("isRescheduledEntry")
    var isRescheduledEntry: Boolean = false,
    @JsonProperty("rescheduledFrom")
    var rescheduledFrom: LocalDate? = null,
    @JsonProperty("rescheduledTo")
    var rescheduledTo: LocalDate? = null,
) {
    constructor(simpleScheduleModel: SimpleScheduleModel) : this(
        simpleScheduleModel.id,
        simpleScheduleModel.userId,
        simpleScheduleModel.startTime,
        simpleScheduleModel.endTime,
        simpleScheduleModel.dayNumber,
        simpleScheduleModel.note,
        null,
    )
}
