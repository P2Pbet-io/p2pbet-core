package com.p2pbet.configuration

import org.apache.activemq.command.ActiveMQQueue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.jms.Queue


@Configuration
class ActiveMQConfiguration {
    @Bean
    fun bscAuctionQueue(): Queue = ActiveMQQueue(BSC_AUCTION_QUEUE)

    @Bean
    fun freeAuctionQueue(): Queue = ActiveMQQueue(FREE_AUCTION_QUEUE)

    @Bean
    fun freeQrGameQueue(): Queue = ActiveMQQueue(FREE_QR_GAME_QUEUE)

    @Bean
    fun bscBinaryQueue(): Queue = ActiveMQQueue(BSC_BINARY_QUEUE)

    @Bean
    fun bscCustomQueue(): Queue = ActiveMQQueue(BSC_CUSTOM_QUEUE)

    @Bean
    fun bscJackpotQueue(): Queue = ActiveMQQueue(BSC_JACKPOT_QUEUE)

    @Bean
    fun polygonAuctionQueue(): Queue = ActiveMQQueue(POLYGON_AUCTION_QUEUE)

    @Bean
    fun polygonBinaryQueue(): Queue = ActiveMQQueue(POLYGON_BINARY_QUEUE)

    @Bean
    fun polygonCustomQueue(): Queue = ActiveMQQueue(POLYGON_CUSTOM_QUEUE)

    @Bean
    fun polygonJackpotQueue(): Queue = ActiveMQQueue(POLYGON_JACKPOT_QUEUE)

    @Bean
    fun notificationQueue(): Queue = ActiveMQQueue(NOTIFICATION_QUEUE)

    companion object {
        const val BSC_AUCTION_QUEUE = "bsc.contract.auction"
        const val BSC_BINARY_QUEUE = "bsc.contract.binary"
        const val BSC_CUSTOM_QUEUE = "bsc.contract.custom"
        const val BSC_JACKPOT_QUEUE = "bsc.contract.jackpot"
        const val POLYGON_AUCTION_QUEUE = "polygon.contract.auction"
        const val POLYGON_BINARY_QUEUE = "polygon.contract.binary"
        const val POLYGON_CUSTOM_QUEUE = "polygon.contract.custom"
        const val POLYGON_JACKPOT_QUEUE = "polygon.contract.jackpot"
        const val AVALANCHE_AUCTION_QUEUE = "avalanche.contract.auction"
        const val AVALANCHE_BINARY_QUEUE = "avalanche.contract.binary"
        const val AVALANCHE_CUSTOM_QUEUE = "avalanche.contract.custom"
        const val AVALANCHE_JACKPOT_QUEUE = "avalanche.contract.jackpot"
        const val FREE_AUCTION_QUEUE = "free.contract.auction"
        const val FREE_QR_GAME_QUEUE = "free.contract.qr.game"
        const val NOTIFICATION_QUEUE = "event.notification"
    }
}
