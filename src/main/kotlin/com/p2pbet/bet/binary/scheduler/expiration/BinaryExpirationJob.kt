package com.p2pbet.bet.binary.scheduler.expiration

import com.p2pbet.bet.binary.scheduler.expiration.dto.BinaryExpirationJobData
import com.p2pbet.bet.binary.service.BinaryBetService
import com.p2pbet.bet.binary.service.BinaryJoinService
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
class BinaryExpirationJob(
    val binaryJoinService: BinaryJoinService,
    val binaryBetService: BinaryBetService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(BinaryExpirationJobData::class.java) }.getOrElse {
            logger.error(it.message, it)
            return
        }

        execute(jobData, context)
    }

    fun execute(jobData: BinaryExpirationJobData, context: JobExecutionContext) {
        runCatching {
            logger.info { "Start binary expiration ${jobData.id}" }
            binaryJoinService.onExpirationBinary(
                binaryId = jobData.id,
                executionType = jobData.executionType
            )
            logger.info { "End of binary expiration ${jobData.id}" }
            context.deleteCurrentJob()
        }.onFailure {
            logger.error("Execution of expiration error", it);
            if (context.isLastTimeTriggerFired()) {
                binaryBetService.markBinaryFailed(jobData.id, jobData.executionType, it.message)
                context.deleteCurrentJob()
            }
        }
    }
}