package com.p2pbet.bet.auction.handler

import com.p2pbet.bet.auction.service.AuctionBetService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.auction.AuctionBetCreatedLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CreateAuctionHandler(
    val actionBetService: AuctionBetService,
) : AuctionAbstractHandler(
    logType = LogEnumMapper.AUCTION_CREATED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as AuctionBetCreatedLog) {
            logger.info { "Start AUCTION_CREATED handler" }
            actionBetService.onCreateAuctionBet(
                createLog = this,
                executionType = executionType
            )
        }
}