package com.p2pbet.client.bi.api.model.custom


data class CloseCustomBetDTO(
    val betId: Long,
    val finalValue: String,
    val targetSideWon: Boolean
)