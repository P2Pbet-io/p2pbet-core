package com.p2pbet.bet.jackpot.scheduler.close

import com.p2pbet.bet.common.entity.enums.ExecutionAction
import com.p2pbet.bet.common.service.BIExecutionService
import com.p2pbet.bet.jackpot.scheduler.close.dto.JackpotCloseJobData
import com.p2pbet.bet.jackpot.service.JackpotActionService
import com.p2pbet.bet.jackpot.service.JackpotBetService
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

@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
class JackpotCloseJob(
    val jackpotBetService: JackpotBetService,
    val jackpotActionService: JackpotActionService,
    val biExecutionService: BIExecutionService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(JackpotCloseJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }
        execute(jobData, context)
    }

    fun execute(jobData: JackpotCloseJobData, context: JobExecutionContext) {
        runCatching {
            logger.info { "Start jackpot close ${jobData.id}" }
            if (jobData.executionId == null) {
                initNewClose(jobData, context)
            } else {
                checkExecutionStatus(jobData, context)
            }
            logger.info { "End of jackpot close ${jobData.id}" }
        }.onFailure {
            logger.error("Execution of close error", it);
            if (context.isLastTimeTriggerFired()) {
                jackpotBetService.markJackpotFailed(jobData.id, jobData.executionType, it.message)
                context.deleteCurrentJob()
            }
        }
    }

    private fun checkExecutionStatus(jobData: JackpotCloseJobData, context: JobExecutionContext) =
        when (biExecutionService.checkStatus(jobData.executionId!!, jobData.executionType)) {
            ExecutionStatus.PENDING,
            ExecutionStatus.SENT,
            -> {
                if (context.isLastTimeTriggerFired()) {
                    jackpotBetService.markJackpotFailed(
                        id = jobData.id,
                        executionType = jobData.executionType,
                        errorMessage = "Async execution failed."
                    )
                    logger.error { "Close jackpot bet with failed status. Expiration timeout." }
                } else {
                    logger.info { "Caught waiting status." }
                }
            }

            ExecutionStatus.CONFIRMED -> {
                logger.info { "Caught successful close write call." }
                context.deleteCurrentJob()
            }

            ExecutionStatus.FAILED -> {
                initNewClose(jobData, context)
            }
        }

    private fun initNewClose(jobData: JackpotCloseJobData, context: JobExecutionContext) {
        logger.info { "Init new close call for auction ${jobData.id}" }

        val executionId = biExecutionService.asyncWriteCall(
            executionAction = ExecutionAction.CLOSE_JACKPOT_BET,
            betId = jobData.id
        ) {
            jackpotActionService.callCloseJackpot(
                betId = jobData.id,
                executionType = jobData.executionType
            )
        }
        jobData.executionId = executionId
        context.updateDataObject(jobData)
    }
}