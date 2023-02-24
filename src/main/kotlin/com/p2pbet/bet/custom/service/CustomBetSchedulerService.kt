package com.p2pbet.bet.custom.service

import com.p2pbet.bet.custom.entity.CustomP2PBetEntity
import com.p2pbet.bet.custom.scheduler.close.CustomCloseJob
import com.p2pbet.bet.custom.scheduler.close.dto.CustomCloseJobData
import com.p2pbet.bet.custom.scheduler.expiration.CustomExpirationJob
import com.p2pbet.bet.custom.scheduler.expiration.dto.CustomExpirationJobData
import com.p2pbet.bet.custom.scheduler.postclose.CustomPostCloseJob
import com.p2pbet.bet.custom.scheduler.postclose.dto.CustomPostCloseJobData
import com.p2pbet.configuration.quartz.SchedulerService
import com.p2pbet.configuration.quartz.TriggerProperties
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CustomBetSchedulerService(
    val schedulerService: SchedulerService,
) {

    fun scheduleCustomExecutionJob(customBet: CustomP2PBetEntity) =
        schedulerService.schedule(
            jobClass = CustomExpirationJob::class.java,
            name = "${CustomExpirationJob::class.java.simpleName}_${customBet.betId}",
            group = CustomExpirationJob::class.java.simpleName,
            data = CustomExpirationJobData(customBet.betId, customBet.baseBet.executionType),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 120,
                priority = 15,
                startAt = customBet.baseBet.expirationDate.plusSeconds(30),
                endAt = customBet.baseBet.expirationDate.plusHours(2)
            )
        )

    fun scheduleCustomCloseJob(customBet: CustomP2PBetEntity) =
        schedulerService.schedule(
            jobClass = CustomCloseJob::class.java,
            name = "${CustomCloseJob::class.java.simpleName}_${customBet.betId}",
            group = CustomCloseJob::class.java.simpleName,
            data = CustomCloseJobData(
                id = customBet.betId,
                executionType = customBet.baseBet.executionType
            ),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 30,
                priority = 15,
                startAt = LocalDateTime.now().plusSeconds(5),
                endAt = customBet.baseBet.expirationDate.plusHours(2)
            )
        )

    fun scheduleCustomPostCloseJob(customBet: CustomP2PBetEntity) =
        schedulerService.schedule(
            jobClass = CustomPostCloseJob::class.java,
            name = "${CustomPostCloseJob::class.java.simpleName}_${customBet.betId}",
            group = CustomPostCloseJob::class.java.simpleName,
            data = CustomPostCloseJobData(customBet.betId, customBet.baseBet.executionType),
            triggerProperties = TriggerProperties(
                repeatCount = -1,
                repeatIntervalInSeconds = 30,
                priority = 15,
                startAt = LocalDateTime.now().plusSeconds(5)
            )
        )
}