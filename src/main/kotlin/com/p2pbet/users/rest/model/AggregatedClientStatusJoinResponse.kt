package com.p2pbet.users.rest.model

import com.p2pbet.users.entity.enums.ClientBetJoinStatus
import com.p2pbet.users.repository.model.AggregatedClientStatusJoin
import java.math.BigDecimal

data class AggregatedClientStatusJoinResponse(
    val joinStatus: ClientBetJoinStatus,
    val totalJoin: BigDecimal,
    val expectedWonAmount: BigDecimal,
    val wonAmountTaken: BigDecimal,
) {
    companion object {
        fun AggregatedClientStatusJoin.toResponse() = AggregatedClientStatusJoinResponse(
            joinStatus = ClientBetJoinStatus.valueOf(joinStatus),
            totalJoin = totalJoin,
            expectedWonAmount = expectedWonAmount,
            wonAmountTaken = wonAmountTaken
        )
    }
}
