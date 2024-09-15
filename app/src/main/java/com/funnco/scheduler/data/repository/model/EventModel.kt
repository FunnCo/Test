package com.funnco.scheduler.data.repository.model


import java.time.LocalDate
import java.time.OffsetTime

data class EventModel(
    var id: String? = null,
    var description: String? = null,
    var startTime: OffsetTime? = null,
    var endTime: OffsetTime? = null,
    var notes: List<NoteModel>? = null,
    var date: LocalDate? = null
)
