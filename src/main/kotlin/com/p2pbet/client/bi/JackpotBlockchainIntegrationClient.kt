package com.p2pbet.client.bi

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.client.bi.api.JackpotWriteApi
import com.p2pbet.client.bi.api.model.execution.ExecutionResponseDTO
import com.p2pbet.client.bi.api.model.jackpot.CloseJackpotBetDTO
import com.p2pbet.client.bi.api.model.jackpot.CreateJackpotBetDTO
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.OffsetDateTime

@Component
class JackpotBlockchainIntegrationClient(
    bscJackpotWriteApi: JackpotWriteApi,
    polygonJackpotWriteApi: JackpotWriteApi,
    avalancheJackpotWriteApi: JackpotWriteApi,
    tronJackpotWriteApi: JackpotWriteApi,
) : AbstractBlockchainIntegrationClient<JackpotWriteApi>(
    bscWriteApi = bscJackpotWriteApi,
    polygonWriteApi = polygonJackpotWriteApi,
    avalancheWriteApi = avalancheJackpotWriteApi,
    tronWriteApi = tronJackpotWriteApi
) {
    fun createJackpotBet(
        eventId: String,
        lockTime: OffsetDateTime,
        expirationTime: OffsetDateTime,
        requestAmount: BigDecimal,
        executionType: BetExecutionType,
    ): ExecutionResponseDTO =
        getApi(executionType).createJackpotBet(
            request = CreateJackpotBetDTO(
                eventId = eventId,
                lockTime = lockTime,
                expirationTime = expirationTime,
                requestAmount = requestAmount
            )
        )

    fun closeJackpotBet(
        betId: Long,
        finalValue: String,
        executionType: BetExecutionType,
    ): ExecutionResponseDTO =
        getApi(executionType).closeJackpotBet(
            request = CloseJackpotBetDTO(
                betId = betId,
                finalValue = finalValue
            )
        )
}