package com.p2pbet.bet.custom.scheduler.expiration

import com.p2pbet.bet.custom.scheduler.expiration.dto.CustomExpirationJobData
import com.p2pbet.bet.custom.service.CustomBetService
import com.p2pbet.bet.custom.service.CustomJoinService
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
class CustomExpirationJob(
    val customBetService: CustomBetService,
    val customJoinService: CustomJoinService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(CustomExpirationJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }

        execute(jobData, context)
    }

    fun execute(jobData: CustomExpirationJobData, context: JobExecutionContext) {
        runCatching {
            logger.info { "Start custom expiration ${jobData.id}" }
            customJoinService.onExpirationCustomBet(
                customId = jobData.id,
                executionType = jobData.executionType
            )
            logger.info { "End of custom expiration ${jobData.id}" }
            context.deleteCurrentJob()
        }.onFailure {
            logger.error("Execution of custom error", it);
            if (context.isLastTimeTriggerFired()) {
                customBetService.markCustomFailed(jobData.id, jobData.executionType, it.message)
                context.deleteCurrentJob()
            }
        }
    }
}