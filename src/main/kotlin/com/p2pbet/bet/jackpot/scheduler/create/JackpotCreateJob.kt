package com.p2pbet.bet.jackpot.scheduler.create

import com.p2pbet.bet.common.entity.enums.ExecutionAction
import com.p2pbet.bet.common.service.BIExecutionService
import com.p2pbet.bet.jackpot.scheduler.create.dto.JackpotCreateJobData
import com.p2pbet.bet.jackpot.service.JackpotActionService
import com.p2pbet.client.bi.api.model.execution.ExecutionStatus
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.deleteCurrentJob
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.getDataObject
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.isLastTimeTriggerFired
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.updateDataObject
import mu.KLogger
import mu.KotlinLogging
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.PersistJobDataAfterExecution
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
class JackpotCreateJob(
    val jackpotActionService: JackpotActionService,
    val biExecutionService: BIExecutionService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(JackpotCreateJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }
        execute(jobData, context)
    }

    fun execute(jobData: JackpotCreateJobData, context: JobExecutionContext) {
        runCatching {
            logger.info { "Start jackpot creation with data: $jobData" }
            if (jobData.executionId == null) {
                initNewCreate(jobData, context)
            } else {
                checkExecutionStatus(jobData, context)
            }
            logger.info { "End of jackpot creation with data: $jobData" }
        }.onFailure {
            logger.error("Execution of close error", it);
            if (context.isLastTimeTriggerFired()) {
                context.deleteCurrentJob()
            }
        }
    }

    private fun checkExecutionStatus(jobData: JackpotCreateJobData, context: JobExecutionContext) =
        when (biExecutionService.checkStatus(jobData.executionId!!, jobData.executionType)) {
            ExecutionStatus.PENDING,
            ExecutionStatus.SENT,
            -> {
                logger.info { "Caught waiting status." }
            }

            ExecutionStatus.CONFIRMED -> {
                logger.info { "Caught successful close jackpot write call." }
                context.deleteCurrentJob()
            }

            ExecutionStatus.FAILED -> {
                logger.info { "Failed during jackpot bet creation with data: $jobData" }
                context.deleteCurrentJob()
            }
        }

    private fun initNewCreate(jobData: JackpotCreateJobData, context: JobExecutionContext) {
        logger.info { "Init new create call for jackpot" }

        val executionId = biExecutionService.asyncWriteCall(
            executionAction = ExecutionAction.CREATE_JACKPOT_BET,
            betId = null
        ) {
            jackpotActionService.callCreateJackpot(
                eventId = jobData.eventId.toString(),
                lockTime = OffsetDateTime.now().plusSeconds(jobData.lockPeriod),
                expirationTime = OffsetDateTime.now().plusSeconds(jobData.lockPeriod + jobData.expirationPeriod),
                requestAmount = jobData.requestAmount,
                executionType = jobData.executionType
            )
        }
        jobData.executionId = executionId
        context.updateDataObject(jobData)
    }
}