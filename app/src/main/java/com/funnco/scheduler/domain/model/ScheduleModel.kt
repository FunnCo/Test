package com.funnco.scheduler.data.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalTime

data class ScheduleModel(
    @JsonProperty("id")
    val id: Int?,
    @JsonProperty("userId")
    val userId : Int,
    @JsonProperty("startTime")
    var startTime: LocalTime = LocalTime.now(),
    @JsonProperty("endTime")
    var endTime: LocalTime = LocalTime.now(),
    @JsonProperty("dayNumber")
    val dayNumber: Int
)
