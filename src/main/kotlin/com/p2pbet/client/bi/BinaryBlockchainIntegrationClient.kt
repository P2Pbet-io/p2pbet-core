package com.p2pbet.client.bi

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.client.bi.api.BinaryWriteApi
import com.p2pbet.client.bi.api.model.binary.CloseBinaryBetDTO
import com.p2pbet.client.bi.api.model.binary.CreateBinaryBetDTO
import com.p2pbet.client.bi.api.model.execution.ExecutionResponseDTO
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class BinaryBlockchainIntegrationClient(
    bscBinaryWriteApi: BinaryWriteApi,
    polygonBinaryWriteApi: BinaryWriteApi,
    avalancheBinaryWriteApi: BinaryWriteApi,
    tronBinaryWriteApi: BinaryWriteApi,
) : AbstractBlockchainIntegrationClient<BinaryWriteApi>(
    bscWriteApi = bscBinaryWriteApi,
    polygonWriteApi = polygonBinaryWriteApi,
    avalancheWriteApi = avalancheBinaryWriteApi,
    tronWriteApi = tronBinaryWriteApi
) {
    fun createBinaryBet(
        eventId: String,
        lockTime: OffsetDateTime,
        expirationTime: OffsetDateTime,
        executionType: BetExecutionType,
    ): ExecutionResponseDTO =
        getApi(executionType).createBinaryBet(
            request = CreateBinaryBetDTO(
                eventId = eventId,
                lockTime = lockTime,
                expirationTime = expirationTime
            )
        )

    fun closeBinaryBet(
        betId: Long,
        lockedValue: String,
        finalValue: String,
        targetSideWon: Boolean,
        executionType: BetExecutionType,
    ): ExecutionResponseDTO =
        getApi(executionType).closeBinaryBet(
            request = CloseBinaryBetDTO(
                betId = betId,
                lockedValue = lockedValue,
                finalValue = finalValue,
                targetSideWon = targetSideWon
            )
        )
}