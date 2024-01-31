package com.funnco.scheduler.domain.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class NoteModel(
    @JsonProperty("id")
    var id: Int?,
    @JsonProperty("note")
    var note: String,
    @JsonProperty("dateOfNote")
    var dateOfNote: LocalDate? = null,
    @JsonProperty("scheduleId")
    var scheduleId: Int,
    @JsonProperty("flag")
    var flag: Boolean
)
