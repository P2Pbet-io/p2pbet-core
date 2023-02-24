package com.p2pbet.bet.auction.service

import com.p2pbet.bet.auction.repository.AuctionJoinRepository
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.client.bi.AuctionBlockchainIntegrationClient
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@Service
class AuctionActionService(
    val auctionBlockchainIntegrationClient: AuctionBlockchainIntegrationClient,
    val auctionBetService: AuctionBetService,
    val auctionJoinService: AuctionJoinRepository,
) {

    fun callCreateAuction(
        eventId: String,
        lockTime: OffsetDateTime,
        expirationTime: OffsetDateTime,
        requestAmount: BigDecimal,
        executionType: BetExecutionType,
    ): UUID =
        auctionBlockchainIntegrationClient.createAuctionBet(
            eventId = eventId,
            lockTime = lockTime,
            expirationTime = expirationTime,
            requestAmount = requestAmount,
            executionType = executionType
        ).id

    fun callCloseAuction(betId: Long, executionType: BetExecutionType): UUID =
        with(auctionBetService.getAuctionBet(betId, executionType)) {
            auctionBlockchainIntegrationClient.closeAuctionBet(
                betId = this.betId,
                finalValue = finalValue!!,
                joinIdsWon = auctionJoinService.findAllByAuctionBetBetIdAndStatusAndExecutionType(
                    betId = betId,
                    status = JoinStatus.WON,
                    executionType = executionType
                ).map {
                    it.joinId.toBigInteger()
                },
                executionType = executionType
            ).id
        }
}
