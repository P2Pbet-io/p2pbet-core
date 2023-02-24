package com.p2pbet.bet.auction.service

import com.p2pbet.bet.auction.entity.AuctionP2PBetEntity
import com.p2pbet.bet.auction.scheduler.close.AuctionCloseJob
import com.p2pbet.bet.auction.scheduler.close.dto.AuctionCloseJobData
import com.p2pbet.bet.auction.scheduler.create.AuctionCreateJob
import com.p2pbet.bet.auction.scheduler.create.dto.AuctionCreateJobData
import com.p2pbet.bet.auction.scheduler.expiration.AuctionExpirationJob
import com.p2pbet.bet.auction.scheduler.expiration.dto.AuctionExpirationJobData
import com.p2pbet.bet.auction.scheduler.postclose.AuctionPostCloseJob
import com.p2pbet.bet.auction.scheduler.postclose.dto.AuctionPostCloseJobData
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.configuration.quartz.SchedulerService
import com.p2pbet.configuration.quartz.TriggerProperties
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class AuctionBetSchedulerService(
    val schedulerService: SchedulerService,
) {
    fun scheduleAuctionCreateJob(
        eventId: UUID,
        lockPeriod: Long,
        expirationPeriod: Long,
        requestAmount: BigDecimal,
        executionType: BetExecutionType,
    ) =
        schedulerService.schedule(
            jobClass = AuctionCreateJob::class.java,
            name = "${AuctionCreateJob::class.java.simpleName}_${UUID.randomUUID()}",
            group = AuctionCreateJob::class.java.simpleName,
            data = AuctionCreateJobData(
                eventId = eventId,
                lockPeriod = lockPeriod,
                expirationPeriod = expirationPeriod,
                requestAmount = requestAmount,
                executionType = executionType
            ),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 10,
                priority = 15,
                startAt = LocalDateTime.now().plusSeconds(1),
                endAt = LocalDateTime.now().plusMinutes(3)
            )
        )

    fun scheduleAuctionExpirationJob(auctionBet: AuctionP2PBetEntity) =
        schedulerService.schedule(
            jobClass = AuctionExpirationJob::class.java,
            name = "${AuctionExpirationJob::class.java.simpleName}_${auctionBet.betId}",
            group = AuctionExpirationJob::class.java.simpleName,
            data = AuctionExpirationJobData(
                id = auctionBet.betId,
                executionType = auctionBet.baseBet.executionType
            ),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 120,
                priority = 15,
                startAt = auctionBet.baseBet.expirationDate,
                endAt = auctionBet.baseBet.expirationDate.plusHours(2)
            )
        )

    fun scheduleAuctionCloseJob(auctionBet: AuctionP2PBetEntity) =
        schedulerService.schedule(
            jobClass = AuctionCloseJob::class.java,
            name = "${AuctionCloseJob::class.java.simpleName}_${auctionBet.betId}",
            group = AuctionCloseJob::class.java.simpleName,
            data = AuctionCloseJobData(
                id = auctionBet.betId,
                executionType = auctionBet.baseBet.executionType
            ),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 30,
                priority = 15,
                startAt = LocalDateTime.now().plusSeconds(2),
                endAt = auctionBet.baseBet.expirationDate.plusHours(2)
            )
        )

    fun scheduleAuctionPostCloseJob(auctionBet: AuctionP2PBetEntity) =
        schedulerService.schedule(
            jobClass = AuctionPostCloseJob::class.java,
            name = "${AuctionPostCloseJob::class.java.simpleName}_${auctionBet.betId}",
            group = AuctionPostCloseJob::class.java.simpleName,
            data = AuctionPostCloseJobData(auctionBet.betId, auctionBet.baseBet.executionType),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 30,
                priority = 10,
                startAt = LocalDateTime.now().plusSeconds(5)
            )
        )
}
