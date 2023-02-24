package com.p2pbet.bet.common.service

import com.p2pbet.bet.common.entity.BIExecutionEntity
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.ExecutionAction
import com.p2pbet.bet.common.repository.BiExecutionRepository
import com.p2pbet.client.bi.ExecutionBlockchainIntegrationClient
import com.p2pbet.client.bi.api.model.execution.ExecutionStatus
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class BIExecutionService(
    val biExecutionRepository: BiExecutionRepository,
    val executionBlockchainIntegrationClient: ExecutionBlockchainIntegrationClient
) {
    private val logger: KLogger = KotlinLogging.logger { }

    fun asyncWriteCall(executionAction: ExecutionAction, betId: Long?, writeCall: () -> UUID): UUID {
        val callId = writeCall()

        val execution = BIExecutionEntity(
            id = callId,
            betId = betId,
            executionStatus = ExecutionStatus.PENDING,
            executionAction = executionAction
        ).apply(biExecutionRepository::save)

        return execution.id
    }

    fun checkStatus(executionId: UUID, executionType: BetExecutionType): ExecutionStatus =
        biExecutionRepository.findById(executionId).get()
            .apply {
                val executionResponse =
                    executionBlockchainIntegrationClient.checkExecutionStatus(executionId, executionType)
                transactionHash = executionResponse.transactionHash
                executionStatus = executionResponse.executionStatus
                errorMessage = executionResponse.errorMessage
            }
            .apply(biExecutionRepository::save)
            .executionStatus
}