package com.p2pbet.users.rest.model

import java.time.LocalDateTime
import java.util.*

data class BetInfo(
    val lockDate: LocalDateTime,
    val expirationDate: LocalDateTime,
    val createdDate: LocalDateTime,
    val finalValue: String?,
    val eventId: UUID,
    val customBetAdditionInfo: CustomBetAdditionInfo?,
)