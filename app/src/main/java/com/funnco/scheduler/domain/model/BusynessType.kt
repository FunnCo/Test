package com.funnco.scheduler.domain.model

enum class BusynessType(val type: Int) {
    BUSY(0),
    PREPARATION(1),
    FINISHING(2),
    FREE(3)
}