package com.funnco.scheduler.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalTime

data class ScheduleModel(
    val id: Int?,
    val userId : Int,
    var startTime: LocalTime,
    var endTime: LocalTime,
    val dayNumber: Int
)
