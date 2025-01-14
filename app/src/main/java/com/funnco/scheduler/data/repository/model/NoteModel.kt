package com.funnco.scheduler.data.repository.model

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class NoteModel(
    var id: String? = null,
    var content: String? = null,
    var flag: Boolean? = false,

    @JsonIgnore
    var canBeDeleted: Boolean = true
) {

    // Нужно, чтобы новые записки не удалялись сразу
    init {
        if(!canBeDeleted) {
            MainScope().launch(Dispatchers.IO) {
                TimeUnit.SECONDS.sleep(10)
                canBeDeleted = true
            }
        }
    }
}