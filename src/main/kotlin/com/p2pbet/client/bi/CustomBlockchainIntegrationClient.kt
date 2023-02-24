package com.p2pbet.client.bi

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.client.bi.api.CustomWriteApi
import com.p2pbet.client.bi.api.model.custom.CloseCustomBetDTO
import com.p2pbet.client.bi.api.model.execution.ExecutionResponseDTO
import org.springframework.stereotype.Component

@Component
class CustomBlockchainIntegrationClient(
    bscCustomWriteApi: CustomWriteApi,
    polygonCustomWriteApi: CustomWriteApi,
    avalancheCustomWriteApi: CustomWriteApi,
    tronCustomWriteApi: CustomWriteApi,
) : AbstractBlockchainIntegrationClient<CustomWriteApi>(
    bscWriteApi = bscCustomWriteApi,
    polygonWriteApi = polygonCustomWriteApi,
    avalancheWriteApi = avalancheCustomWriteApi,
    tronWriteApi = tronCustomWriteApi
) {
    fun closeCustomBet(
        betId: Long,
        finalValue: String,
        targetSideWon: Boolean,
        executionType: BetExecutionType,
    ): ExecutionResponseDTO =
        getApi(executionType).closeCustomBet(
            request = CloseCustomBetDTO(
                betId = betId,
                finalValue = finalValue,
                targetSideWon = targetSideWon
            )
        )
}