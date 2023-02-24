package com.p2pbet.bet.binary.service

import com.p2pbet.bet.binary.entity.BinaryP2PBetEntity
import com.p2pbet.bet.binary.scheduler.close.BinaryCloseJob
import com.p2pbet.bet.binary.scheduler.close.dto.BinaryCloseJobData
import com.p2pbet.bet.binary.scheduler.create.BinaryCreateJob
import com.p2pbet.bet.binary.scheduler.create.dto.BinaryCreateJobData
import com.p2pbet.bet.binary.scheduler.expiration.BinaryExpirationJob
import com.p2pbet.bet.binary.scheduler.expiration.dto.BinaryExpirationJobData
import com.p2pbet.bet.binary.scheduler.postclose.BinaryPostCloseJob
import com.p2pbet.bet.binary.scheduler.postclose.dto.BinaryPostCloseJobData
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.configuration.quartz.SchedulerService
import com.p2pbet.configuration.quartz.TriggerProperties
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class BinaryBetSchedulerService(
    val schedulerService: SchedulerService,
) {
    fun scheduleBinaryCreateJob(
        eventId: UUID,
        lockPeriod: Long,
        expirationPeriod: Long,
        executionType: BetExecutionType,
    ) =
        schedulerService.schedule(
            jobClass = BinaryCreateJob::class.java,
            name = "${BinaryCreateJob::class.java.simpleName}_${UUID.randomUUID()}",
            group = BinaryCreateJob::class.java.simpleName,
            data = BinaryCreateJobData(
                eventId = eventId,
                lockPeriod = lockPeriod,
                expirationPeriod = expirationPeriod,
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

    fun scheduleBinaryExpirationJob(binaryBet: BinaryP2PBetEntity) =
        schedulerService.schedule(
            jobClass = BinaryExpirationJob::class.java,
            name = "${BinaryExpirationJob::class.java.simpleName}_${binaryBet.betId}",
            group = BinaryExpirationJob::class.java.simpleName,
            data = BinaryExpirationJobData(binaryBet.betId, binaryBet.baseBet.executionType),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 120,
                priority = 15,
                startAt = binaryBet.baseBet.expirationDate.plusSeconds(30),
                endAt = binaryBet.baseBet.expirationDate.plusHours(2)
            )
        )

    fun scheduleBinaryCloseJob(binaryBet: BinaryP2PBetEntity) =
        schedulerService.schedule(
            jobClass = BinaryCloseJob::class.java,
            name = "${BinaryCloseJob::class.java.simpleName}_${binaryBet.betId}",
            group = BinaryCloseJob::class.java.simpleName,
            data = BinaryCloseJobData(
                id = binaryBet.betId,
                executionType = binaryBet.baseBet.executionType
            ),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 30,
                priority = 15,
                startAt = LocalDateTime.now().plusSeconds(5),
                endAt = binaryBet.baseBet.expirationDate.plusHours(2)
            )
        )

    fun scheduleBinaryPostCloseJob(binaryBet: BinaryP2PBetEntity) =
        schedulerService.schedule(
            jobClass = BinaryPostCloseJob::class.java,
            name = "${BinaryPostCloseJob::class.java.simpleName}_${binaryBet.betId}",
            group = BinaryPostCloseJob::class.java.simpleName,
            data = BinaryPostCloseJobData(binaryBet.betId, binaryBet.baseBet.executionType),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 30,
                priority = 10,
                startAt = LocalDateTime.now().plusSeconds(5)
            )
        )
}