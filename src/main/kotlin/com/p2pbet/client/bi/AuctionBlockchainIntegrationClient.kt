package com.p2pbet.client.bi

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.client.bi.api.AuctionWriteApi
import com.p2pbet.client.bi.api.model.auction.CloseAuctionBetDTO
import com.p2pbet.client.bi.api.model.auction.CreateAuctionBetDTO
import com.p2pbet.client.bi.api.model.execution.ExecutionResponseDTO
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigInteger
import java.time.OffsetDateTime

@Component
class AuctionBlockchainIntegrationClient(
    bscAuctionWriteApi: AuctionWriteApi,
    polygonAuctionWriteApi: AuctionWriteApi,
    avalancheAuctionWriteApi: AuctionWriteApi,
    tronAuctionWriteApi: AuctionWriteApi,
) : AbstractBlockchainIntegrationClient<AuctionWriteApi>(
    bscWriteApi = bscAuctionWriteApi,
    polygonWriteApi = polygonAuctionWriteApi,
    avalancheWriteApi = avalancheAuctionWriteApi,
    tronWriteApi = tronAuctionWriteApi
) {
    fun createAuctionBet(
        eventId: String,
        lockTime: OffsetDateTime,
        expirationTime: OffsetDateTime,
        requestAmount: BigDecimal,
        executionType: BetExecutionType,
    ): ExecutionResponseDTO =
        getApi(executionType).createAuctionBet(
            request = CreateAuctionBetDTO(
                eventId = eventId,
                lockTime = lockTime,
                expirationTime = expirationTime,
                requestAmount = requestAmount
            )
        )

    fun closeAuctionBet(
        betId: Long,
        finalValue: String,
        joinIdsWon: List<BigInteger>,
        executionType: BetExecutionType,
    ): ExecutionResponseDTO =
        getApi(executionType).closeAuctionBet(
            request = CloseAuctionBetDTO(
                betId = betId,
                finalValue = finalValue,
                joinIdsWon = joinIdsWon
            )
        )
}