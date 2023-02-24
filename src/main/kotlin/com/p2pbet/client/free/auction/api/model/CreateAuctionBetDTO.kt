package com.p2pbet.client.free.auction.api.model

import com.p2pbet.messaging.model.queue.ContractType
import java.math.BigDecimal
import java.time.LocalDateTime

class CreateAuctionBetDTO(
    val eventId: String,
    val lockTime: LocalDateTime,
    val expirationTime: LocalDateTime,
    val requestAmount: BigDecimal,
) : BaseExecutionModel(
    contractType = ContractType.AUCTION
)