package com.p2pbet.bet.custom.service

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.client.bi.CustomBlockchainIntegrationClient
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomActionService(
    val customBlockchainIntegrationClient: CustomBlockchainIntegrationClient,
    val customBetService: CustomBetService,
) {

    fun callCloseCustom(betId: Long, executionType: BetExecutionType): UUID =
        with(customBetService.getCustomBet(betId, executionType)) {
            customBlockchainIntegrationClient.closeCustomBet(
                betId = this.betId,
                finalValue = finalValue!!,
                targetSideWon = targetSideWon!!,
                executionType = executionType
            ).id
        }
}