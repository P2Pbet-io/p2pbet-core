package com.p2pbet.client.bi.api.model.binary

import java.time.OffsetDateTime

class CreateBinaryBetDTO(
    val eventId: String,
    val lockTime: OffsetDateTime,
    val expirationTime: OffsetDateTime
)