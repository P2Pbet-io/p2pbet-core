package com.p2pbet.bet.auction.handler

import com.p2pbet.bet.auction.service.AuctionJoinService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.auction.AuctionBetPrizeTakenLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class TakePrizeAuctionHandler(
    val auctionJoinService: AuctionJoinService
) : AuctionAbstractHandler(
    logType = LogEnumMapper.AUCTION_PRIZE_TAKEN
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as AuctionBetPrizeTakenLog) {
            logger.info { "Start AUCTION_PRIZE_TAKEN handler" }
            auctionJoinService.onAuctionPrizeTaken(this, executionType)
        }
}