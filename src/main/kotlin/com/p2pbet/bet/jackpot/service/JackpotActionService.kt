package com.p2pbet.bet.jackpot.service

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.client.bi.JackpotBlockchainIntegrationClient
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@Service
class JackpotActionService(
    val jackpotBlockchainIntegrationClient: JackpotBlockchainIntegrationClient,
    val jackpotBetService: JackpotBetService,
) {

    fun callCreateJackpot(
        eventId: String,
        lockTime: OffsetDateTime,
        expirationTime: OffsetDateTime,
        requestAmount: BigDecimal,
        executionType: BetExecutionType,
    ): UUID =
        jackpotBlockchainIntegrationClient.createJackpotBet(
            eventId = eventId,
            lockTime = lockTime,
            expirationTime = expirationTime,
            requestAmount = requestAmount,
            executionType = executionType
        ).id

    fun callCloseJackpot(betId: Long, executionType: BetExecutionType): UUID =
        with(jackpotBetService.getJackpotBet(betId, executionType)) {
            jackpotBlockchainIntegrationClient.closeJackpotBet(
                betId = this.betId,
                finalValue = finalValue!!,
                executionType = executionType
            ).id
        }
}
