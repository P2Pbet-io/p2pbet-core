package com.p2pbet.util.filter.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.p2pbet.bet.common.entity.enums.JoinStatus

class FilteredJoinStatusList(
    val list: List<JoinStatus>,
) {

    override fun toString(): String = jacksonObjectMapper().writeValueAsString(this)

    companion object {
        fun String.fromString(): FilteredJoinStatusList =
            jacksonObjectMapper().readValue(this, FilteredJoinStatusList::class.java)
    }
}