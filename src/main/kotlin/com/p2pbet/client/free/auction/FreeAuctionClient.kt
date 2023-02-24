package com.p2pbet.client.free.auction

import com.p2pbet.client.bi.api.model.auction.CloseAuctionBetDTO
import com.p2pbet.client.free.auction.api.FreeAuctionApi
import com.p2pbet.client.free.auction.api.model.CreateAuctionBetDTO
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class FreeAuctionClient(
    private val freeAuctionApi: FreeAuctionApi,
) {
    fun createAuctionBet(
        eventId: String,
        lockTime: LocalDateTime,
        expirationTime: LocalDateTime,
        requestAmount: BigDecimal,
    ) {
        freeAuctionApi.createAuctionBet(
            request = CreateAuctionBetDTO(
                eventId = eventId,
                lockTime = lockTime,
                expirationTime = expirationTime,
                requestAmount = requestAmount
            )
        )
    }

    fun closeAuctionBet(
        betId: Long,
        finalValue: String,
        joinIdsWon: List<BigInteger>,
    ) {
        freeAuctionApi.closeAuctionBet(
            request = CloseAuctionBetDTO(
                betId = betId,
                finalValue = finalValue,
                joinIdsWon = joinIdsWon
            )
        )
    }
}