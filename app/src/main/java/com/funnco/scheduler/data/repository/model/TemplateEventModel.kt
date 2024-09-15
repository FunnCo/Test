package com.funnco.scheduler.data.repository.model


import java.time.OffsetTime


data class TemplateEventModel(
    var id: String? = null,
    var description: String? = null,
    var startTime: OffsetTime? = null,
    var endTime: OffsetTime? = null,
    var day: String? = null
)