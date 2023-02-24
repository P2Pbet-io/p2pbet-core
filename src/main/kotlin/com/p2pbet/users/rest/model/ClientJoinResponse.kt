package com.p2pbet.users.rest.model

import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.users.entity.enums.ClientBetJoinStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class ClientJoinResponse(
    val betType: ContractType,
    val betId: Long,
    val betInfo: BetInfo,
    val totalJoinAmount: BigDecimal,
    val joinStatus: ClientBetJoinStatus,
    val expectedWonAmount: BigDecimal?,
    val amountTaken: BigDecimal?,
    val modifiedDate: LocalDateTime,
    val createdDate: LocalDateTime,
)
