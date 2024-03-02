package com.funnco.scheduler.domain.model

import com.funnco.scheduler.data.model.ScheduleModel

data class FullScheduleContainer (
    val id: Int,
    val dayNumber: Int,
    val scheduleEntries: List<ScheduleModel>
)