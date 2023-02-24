package com.p2pbet.client.bi.api.model.execution

import com.p2pbet.messaging.model.queue.ContractType
import java.util.*

data class ExecutionResponseDTO(
    val id: UUID,
    val contractType: ContractType,
    val executionStatus: ExecutionStatus,
    val transactionHash: String?,
    val errorMessage: String?
)
