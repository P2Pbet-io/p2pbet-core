package com.p2pbet.bet.jackpot.scheduler.expiration

import com.p2pbet.bet.jackpot.scheduler.expiration.dto.JackpotExpirationJobData
import com.p2pbet.bet.jackpot.service.JackpotBetService
import com.p2pbet.bet.jackpot.service.JackpotJoinService
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.deleteCurrentJob
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.getDataObject
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.isLastTimeTriggerFired
import mu.KLogger
import mu.KotlinLogging
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component

@Component
@DisallowConcurrentExecution
class JackpotExpirationJob(
    val jackpotBetService: JackpotBetService,
    val jackpotJoinService: JackpotJoinService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(JackpotExpirationJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }

        execute(jobData, context)
    }

    fun execute(jobData: JackpotExpirationJobData, context: JobExecutionContext) {
        runCatching {
            logger.info { "Start jackpot expiration ${jobData.id}" }
            jackpotJoinService.onExpirationJackpot(
                jackpotId = jobData.id,
                executionType = jobData.executionType
            )
            logger.info { "End of jackpot expiration ${jobData.id}" }
            context.deleteCurrentJob()
        }.onFailure {
            logger.error("Execution of expiration jackpot error", it);
            if (context.isLastTimeTriggerFired()) {
                jackpotBetService.markJackpotFailed(jobData.id, jobData.executionType, it.message)
                context.deleteCurrentJob()
            }
        }
    }
}