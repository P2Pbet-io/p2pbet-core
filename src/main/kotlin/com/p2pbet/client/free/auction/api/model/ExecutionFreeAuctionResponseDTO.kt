package com.p2pbet.client.free.auction.api.model

import com.p2pbet.client.bi.api.model.execution.ExecutionStatus
import com.p2pbet.messaging.model.queue.ContractType

data class ExecutionFreeAuctionResponseDTO(
    val contractType: ContractType,
    val executionStatus: ExecutionStatus,
)
