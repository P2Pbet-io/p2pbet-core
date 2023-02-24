package com.p2pbet.bet.custom.scheduler.close

import com.p2pbet.bet.common.entity.enums.ExecutionAction
import com.p2pbet.bet.common.service.BIExecutionService
import com.p2pbet.bet.custom.scheduler.close.dto.CustomCloseJobData
import com.p2pbet.bet.custom.service.CustomActionService
import com.p2pbet.bet.custom.service.CustomBetService
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
class CustomCloseJob(
    val customBetService: CustomBetService,
    val customActionService: CustomActionService,
    val biExecutionService: BIExecutionService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(CustomCloseJobData::class.java) }.getOrElse {
            logger.error(it.message, it)
            return
        }
        execute(jobData, context)
    }

    fun execute(jobData: CustomCloseJobData, context: JobExecutionContext) {
        runCatching {
            logger.info { "Start custom close ${jobData.id}" }
            if (jobData.executionId == null) {
                initNewClose(jobData, context)
            } else {
                checkExecutionStatus(jobData, context)
            }
            logger.info { "End of custom close ${jobData.id}" }
        }.onFailure {
            logger.error("Execution of custom bet close error", it);
            if (context.isLastTimeTriggerFired()) {
                customBetService.markCustomFailed(jobData.id, jobData.executionType, it.message)
                context.deleteCurrentJob()
            }
        }
    }

    private fun checkExecutionStatus(jobData: CustomCloseJobData, context: JobExecutionContext) =
        when (biExecutionService.checkStatus(
            executionId = jobData.executionId!!,
            executionType = jobData.executionType
        )) {
            ExecutionStatus.PENDING, ExecutionStatus.SENT -> {
                if (context.isLastTimeTriggerFired()) {
                    customBetService.markCustomFailed(
                        id = jobData.id,
                        errorMessage = "Async execution failed.",
                        executionType = jobData.executionType
                    )
                    logger.error { "Close custom bet with failed status. Expiration timeout." }
                } else {
                    logger.info { "Caught waiting status." }
                }
            }

            ExecutionStatus.CONFIRMED -> {
                logger.info { "Caught successful close custom write call." }
                context.deleteCurrentJob()
            }

            ExecutionStatus.FAILED -> {
                initNewClose(jobData, context)
            }
        }

    private fun initNewClose(jobData: CustomCloseJobData, context: JobExecutionContext) {
        logger.info { "Init new close call for custom ${jobData.id}" }

        val executionId = biExecutionService.asyncWriteCall(
            executionAction = ExecutionAction.CLOSE_CUSTOM_BET, betId = jobData.id
        ) {
            customActionService.callCloseCustom(
                betId = jobData.id,
                executionType = jobData.executionType
            )
        }
        jobData.executionId = executionId
        context.updateDataObject(jobData)
    }
}