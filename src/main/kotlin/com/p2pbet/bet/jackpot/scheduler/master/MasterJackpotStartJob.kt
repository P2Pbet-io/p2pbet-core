package com.p2pbet.bet.jackpot.scheduler.master

import com.p2pbet.bet.jackpot.scheduler.master.dto.MasterJackpotStartJobData
import com.p2pbet.bet.jackpot.service.JackpotBetService
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.getDataObject
import mu.KLogger
import mu.KotlinLogging
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component

@Component
@DisallowConcurrentExecution
class MasterJackpotStartJob(
    val jackpotBetService: JackpotBetService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(MasterJackpotStartJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }
        jackpotBetService.creatingJackpotBet(
            eventId = jobData.eventId,
            lockPeriod = jobData.lockPeriod,
            expirationPeriod = jobData.expirationPeriod,
            requestAmount = jobData.requestAmount,
            executionType = jobData.executionType
        )
    }
}