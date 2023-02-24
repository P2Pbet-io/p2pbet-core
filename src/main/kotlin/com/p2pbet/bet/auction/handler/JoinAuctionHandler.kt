package com.p2pbet.bet.auction.handler

import com.p2pbet.bet.auction.service.AuctionJoinService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.auction.AuctionBetJoinedLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class JoinAuctionHandler(
    val auctionJoinService: AuctionJoinService
) : AuctionAbstractHandler(
    logType = LogEnumMapper.AUCTION_JOINED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as AuctionBetJoinedLog) {
            logger.info { "Start AUCTION_JOINED handler" }
            auctionJoinService.onAuctionJoin(this, executionType)
        }
}