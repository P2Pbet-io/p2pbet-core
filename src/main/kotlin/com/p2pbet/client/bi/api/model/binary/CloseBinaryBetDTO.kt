package com.p2pbet.client.bi.api.model.binary


data class CloseBinaryBetDTO(
    val betId: Long,
    val lockedValue: String,
    val finalValue: String,
    val targetSideWon: Boolean
)