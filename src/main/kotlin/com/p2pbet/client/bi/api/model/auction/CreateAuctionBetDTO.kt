package com.p2pbet.client.bi.api.model.auction

import java.math.BigDecimal
import java.time.OffsetDateTime

class CreateAuctionBetDTO(
    val eventId: String,
    val lockTime: OffsetDateTime,
    val expirationTime: OffsetDateTime,
    val requestAmount: BigDecimal
)