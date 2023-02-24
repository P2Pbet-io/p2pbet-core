package com.p2pbet.util.filter.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.p2pbet.bet.jackpot.entity.enums.JackpotJoinStatus

class FilteredJackpotJoinStatusList(
    val list: List<JackpotJoinStatus>,
) {

    override fun toString(): String = jacksonObjectMapper().writeValueAsString(this)

    companion object {
        fun String.fromString(): FilteredJackpotJoinStatusList =
            jacksonObjectMapper().readValue(this, FilteredJackpotJoinStatusList::class.java)
    }
}