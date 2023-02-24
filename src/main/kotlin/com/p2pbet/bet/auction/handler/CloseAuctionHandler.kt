package com.p2pbet.bet.auction.handler

import com.p2pbet.bet.auction.service.AuctionBetService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.auction.AuctionBetClosedLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CloseAuctionHandler(
    val actionBetService: AuctionBetService
) : AuctionAbstractHandler(
    logType = LogEnumMapper.AUCTION_CLOSED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as AuctionBetClosedLog) {
            logger.info { "Start AUCTION_CLOSED handler" }
            actionBetService.onCloseAuctionBet(this, executionType)
        }
}