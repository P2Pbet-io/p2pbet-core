package com.p2pbet.bet.auction.service

import com.p2pbet.bet.auction.repository.AuctionJoinRepository
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.client.free.auction.FreeAuctionClient
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.OffsetDateTime

@Service
class FreeAuctionActionService(
    val freeAuctionClient: FreeAuctionClient,
    val auctionBetService: AuctionBetService,
    val auctionJoinService: AuctionJoinRepository,
) {

    fun callCreateAuction(
        eventId: String,
        lockTime: OffsetDateTime,
        expirationTime: OffsetDateTime,
        requestAmount: BigDecimal,
    ): Unit =
        freeAuctionClient.createAuctionBet(
            eventId = eventId,
            lockTime = lockTime.toLocalDateTime(),
            expirationTime = expirationTime.toLocalDateTime(),
            requestAmount = requestAmount
        )

    fun callCloseAuction(betId: Long): Unit = with(auctionBetService.getAuctionBet(betId, BetExecutionType.FREE)) {
        freeAuctionClient.closeAuctionBet(
            betId = this.betId,
            finalValue = finalValue!!,
            joinIdsWon = auctionJoinService.findAllByAuctionBetBetIdAndStatusAndExecutionType(
                betId = betId,
                status = JoinStatus.WON,
                executionType = BetExecutionType.FREE
            ).map {
                it.joinId.toBigInteger()
            }
        )
    }
}
