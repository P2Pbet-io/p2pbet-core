package com.p2pbet.p2pevent.controller.model

import com.p2pbet.p2pevent.entity.BaseEvent
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class BaseEventWithPopularityResponse(
    var id: UUID = UUID.randomUUID(),
    val type: EventType,
    val symbol: String,
    val fullName: String,
    val name: String,
    val createdDate: LocalDateTime,
    val finBetCount: Int,
    val finBetPool: BigDecimal,
    val srcUrl: String,
) {
    constructor(event: BaseEvent, finBetCount: Int, finBetPool: BigDecimal) : this(
        id = event.id,
        type = event.type,
        symbol = event.symbol,
        fullName = event.fullName,
        name = event.name,
        createdDate = event.createdDate,
        finBetCount = finBetCount,
        srcUrl = event.srcUrl,
        finBetPool = finBetPool
    )
}
