package com.p2pbet.bet.auction.handler

import com.p2pbet.bet.auction.service.AuctionJoinService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.auction.AuctionBetRefundedLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class RefundAuctionHandler(
    val auctionJoinService: AuctionJoinService
) : AuctionAbstractHandler(
    logType = LogEnumMapper.AUCTION_REFUNDED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as AuctionBetRefundedLog) {
            logger.info { "Start BINARY_REFUNDED handler" }
            auctionJoinService.onAuctionRefund(this, executionType)
        }
}