package com.p2pbet.bet.auction.handler

import com.p2pbet.bet.common.handler.ActionBetHandler
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging

abstract class AuctionAbstractHandler(
    val logType: LogEnumMapper,
) : ActionBetHandler {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun isSupport(contractType: ContractType, logType: LogEnumMapper): Boolean =
        contractType == ContractType.AUCTION && logType == this.logType
}