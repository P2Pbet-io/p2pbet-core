package com.p2pbet.bet.jackpot.service

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.jackpot.entity.JackpotP2PBetEntity
import com.p2pbet.bet.jackpot.scheduler.close.JackpotCloseJob
import com.p2pbet.bet.jackpot.scheduler.close.dto.JackpotCloseJobData
import com.p2pbet.bet.jackpot.scheduler.create.JackpotCreateJob
import com.p2pbet.bet.jackpot.scheduler.create.dto.JackpotCreateJobData
import com.p2pbet.bet.jackpot.scheduler.expiration.JackpotExpirationJob
import com.p2pbet.bet.jackpot.scheduler.expiration.dto.JackpotExpirationJobData
import com.p2pbet.bet.jackpot.scheduler.postclose.JackpotPostCloseJob
import com.p2pbet.bet.jackpot.scheduler.postclose.dto.JackpotPostCloseJobData
import com.p2pbet.configuration.quartz.SchedulerService
import com.p2pbet.configuration.quartz.TriggerProperties
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class JackpotBetSchedulerService(
    val schedulerService: SchedulerService,
) {
    fun scheduleJackpotCreateJob(
        eventId: UUID,
        lockPeriod: Long,
        expirationPeriod: Long,
        requestAmount: BigDecimal,
        executionType: BetExecutionType,
    ) =
        schedulerService.schedule(
            jobClass = JackpotCreateJob::class.java,
            name = "${JackpotCreateJob::class.java.simpleName}_${UUID.randomUUID()}",
            group = JackpotCreateJob::class.java.simpleName,
            data = JackpotCreateJobData(
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
                startAt = LocalDateTime.now().plusSeconds(5),
                endAt = LocalDateTime.now().plusMinutes(3)
            )
        )

    fun scheduleJackpotExpirationJob(jackpotBet: JackpotP2PBetEntity) =
        schedulerService.schedule(
            jobClass = JackpotExpirationJob::class.java,
            name = "${JackpotExpirationJob::class.java.simpleName}_${jackpotBet.betId}",
            group = JackpotExpirationJob::class.java.simpleName,
            data = JackpotExpirationJobData(jackpotBet.betId, jackpotBet.baseBet.executionType),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 120,
                priority = 15,
                startAt = jackpotBet.baseBet.expirationDate.plusSeconds(30),
                endAt = jackpotBet.baseBet.expirationDate.plusHours(2)
            )
        )

    fun scheduleJackpotCloseJob(jackpotBet: JackpotP2PBetEntity) =
        schedulerService.schedule(
            jobClass = JackpotCloseJob::class.java,
            name = "${JackpotCloseJob::class.java.simpleName}_${jackpotBet.betId}",
            group = JackpotCloseJob::class.java.simpleName,
            data = JackpotCloseJobData(
                id = jackpotBet.betId,
                executionType = jackpotBet.baseBet.executionType
            ),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 30,
                priority = 15,
                startAt = LocalDateTime.now().plusSeconds(5),
                endAt = jackpotBet.baseBet.expirationDate.plusHours(2)
            )
        )

    fun scheduleJackpotPostCloseJob(jackpotBet: JackpotP2PBetEntity) =
        schedulerService.schedule(
            jobClass = JackpotPostCloseJob::class.java,
            name = "${JackpotPostCloseJob::class.java.simpleName}_${jackpotBet.betId}",
            group = JackpotPostCloseJob::class.java.simpleName,
            data = JackpotPostCloseJobData(jackpotBet.betId, jackpotBet.baseBet.executionType),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 30,
                priority = 10,
                startAt = LocalDateTime.now().plusSeconds(5)
            )
        )
}