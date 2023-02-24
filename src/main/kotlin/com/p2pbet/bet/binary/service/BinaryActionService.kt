package com.p2pbet.bet.binary.service

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.client.bi.BinaryBlockchainIntegrationClient
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*

@Service
class BinaryActionService(
    val binaryBlockchainIntegrationClient: BinaryBlockchainIntegrationClient,
    val binaryBetService: BinaryBetService,
) {

    fun callCloseBinary(betId: Long, executionType: BetExecutionType): UUID =
        with(binaryBetService.getBinaryBet(betId, executionType)) {
            binaryBlockchainIntegrationClient.closeBinaryBet(
                betId = this.betId,
                lockedValue = lockedValue!!,
                finalValue = finalValue!!,
                targetSideWon = sideWon!!,
                executionType = executionType
            ).id
        }

    fun callCreateBinary(
        eventId: String,
        lockTime: OffsetDateTime,
        expirationTime: OffsetDateTime,
        executionType: BetExecutionType,
    ): UUID =
        binaryBlockchainIntegrationClient.createBinaryBet(
            eventId = eventId,
            lockTime = lockTime,
            expirationTime = expirationTime,
            executionType = executionType
        ).id

}