package com.p2pbet.util.filter.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.p2pbet.bet.common.entity.enums.BetStatus

class FilteredBetStatusList(
    val list: List<BetStatus>,
) {

    override fun toString(): String = jacksonObjectMapper().writeValueAsString(this)

    companion object {
        fun String.fromString(): FilteredBetStatusList =
            jacksonObjectMapper().readValue(this, FilteredBetStatusList::class.java)
    }
}