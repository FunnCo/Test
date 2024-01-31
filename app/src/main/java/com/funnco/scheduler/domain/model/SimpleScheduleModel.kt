package com.funnco.scheduler.domain.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.funnco.scheduler.data.model.ScheduleModel
import java.time.LocalTime

data class SimpleScheduleModel(
    @JsonProperty("id")
    val id: Int?,
    @JsonProperty("userId")
    val userId : Int,
    @JsonProperty("startTime")
    var startTime: LocalTime = LocalTime.now(),
    @JsonProperty("endTime")
    var endTime: LocalTime = LocalTime.now(),
    @JsonProperty("dayNumber")
    val dayNumber: Int,
    @JsonProperty("note")
    var note: String? = null,
) {
    constructor(scheduleModel: ScheduleModel): this(
        scheduleModel.id,
        scheduleModel.userId,
        scheduleModel.startTime,
        scheduleModel.endTime,
        scheduleModel.dayNumber,
        scheduleModel.note
    )
}
