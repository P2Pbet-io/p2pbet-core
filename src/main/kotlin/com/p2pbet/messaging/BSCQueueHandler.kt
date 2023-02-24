package com.p2pbet.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.handler.BetHandler
import com.p2pbet.configuration.ActiveMQConfiguration.Companion.BSC_AUCTION_QUEUE
import com.p2pbet.configuration.ActiveMQConfiguration.Companion.BSC_BINARY_QUEUE
import com.p2pbet.configuration.ActiveMQConfiguration.Companion.BSC_CUSTOM_QUEUE
import com.p2pbet.configuration.ActiveMQConfiguration.Companion.BSC_JACKPOT_QUEUE
import com.p2pbet.messaging.model.queue.ContractType
import mu.KLogger
import mu.KotlinLogging
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component


@Component
class BSCQueueHandler(
    override val objectMapper: ObjectMapper,
    val betHandler: BetHandler,
) : AbstractQueueHandler(objectMapper) {
    private val logger: KLogger = KotlinLogging.logger { }


    @JmsListener(destination = BSC_AUCTION_QUEUE)
    fun receiveAuctionMessage(message: String) {
        logger.info { "Caught from bsc AUCTION_QUEUE: $message" }
        val auctionLogDTO = extractMessage(message)
        betHandler.handler(auctionLogDTO, ContractType.AUCTION, BetExecutionType.BSC)
    }

    @JmsListener(destination = BSC_BINARY_QUEUE)
    fun receiveBinaryMessage(message: String) {
        logger.info { "Caught from bsc BINARY_QUEUE: $message" }
        val binaryLogDTO = extractMessage(message)
        betHandler.handler(binaryLogDTO, ContractType.BINARY, BetExecutionType.BSC)
    }

    @JmsListener(destination = BSC_CUSTOM_QUEUE)
    fun receiveCustomMessage(message: String) {
        logger.info { "Caught from bsc CUSTOM_QUEUE: $message" }
        val customLogDTO = extractMessage(message)
        betHandler.handler(customLogDTO, ContractType.CUSTOM, BetExecutionType.BSC)
    }

    @JmsListener(destination = BSC_JACKPOT_QUEUE)
    fun receiveJackpotMessage(message: String) {
        logger.info { "Caught from bsc JACKPOT_QUEUE: $message" }
        val jackpotLogDTO = extractMessage(message)
        betHandler.handler(jackpotLogDTO, ContractType.JACKPOT, BetExecutionType.BSC)
    }
}