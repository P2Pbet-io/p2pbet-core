package com.p2pbet.users.rest.model

import com.p2pbet.messaging.model.queue.ContractType
import java.time.LocalDateTime

data class ClientTransactionResponse(
    val betType: ContractType,
    val betId: Long,
    val betInfo: BetInfo,
    val logSc: Map<*, *>,
    val createdDate: LocalDateTime,
)

