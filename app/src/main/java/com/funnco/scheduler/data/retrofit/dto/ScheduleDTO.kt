package com.funnco.scheduler.data.retrofit.dto

import com.funnco.scheduler.data.model.ScheduleModel
import com.google.gson.annotations.SerializedName
import java.time.LocalTime

data class ScheduleDTO (
    @SerializedName("id")
    val id: Int?,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("day_number")
    val dayNumber: Int
) {

    constructor(model: ScheduleModel): this(model.id, model.userId, model.startTime.toString(), model.endTime.toString(), model.dayNumber)

}