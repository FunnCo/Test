package com.funnco.scheduler.data.retrofit.dto

import com.google.gson.annotations.SerializedName

data class ScheduleDTO (
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("day_number")
    val dayNumber: Int
)