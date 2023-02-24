package com.p2pbet.util.filter.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*

data class FilteredBaseEvent(
    val eventId: UUID,
) {
    override fun toString(): String = jacksonObjectMapper().writeValueAsString(this)
}
