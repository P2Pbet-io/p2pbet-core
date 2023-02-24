package com.p2pbet.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.handler.BetHandler
import com.p2pbet.configuration.ActiveMQConfiguration
import com.p2pbet.messaging.model.queue.ContractType
import mu.KLogger
import mu.KotlinLogging
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component


@Component
class FreeQueueHandler(
    override val objectMapper: ObjectMapper,
    val betHandler: BetHandler,
) : AbstractQueueHandler(objectMapper) {
    private val logger: KLogger = KotlinLogging.logger { }


    @JmsListener(destination = ActiveMQConfiguration.FREE_AUCTION_QUEUE)
    fun receiveFreeAuctionMessage(message: String) {
        logger.info { "Caught from free FREE_AUCTION_QUEUE: $message" }
        val auctionLogDTO = extractMessage(message)
        betHandler.handler(auctionLogDTO, ContractType.AUCTION, BetExecutionType.FREE)
    }

    @JmsListener(destination = ActiveMQConfiguration.FREE_QR_GAME_QUEUE)
    fun receiveFreeQrGameMessage(message: String) {
        logger.info { "Caught from free FREE_QR_GAME_QUEUE: $message" }
        val auctionLogDTO = extractMessage(message)
        betHandler.handler(auctionLogDTO, ContractType.AUCTION, BetExecutionType.FREE_QR)
    }
}
