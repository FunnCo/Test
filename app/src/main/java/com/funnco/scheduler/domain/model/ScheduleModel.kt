package com.funnco.scheduler.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalTime

data class ScheduleModel(
    val userId : Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val dayNumber: Int
)
