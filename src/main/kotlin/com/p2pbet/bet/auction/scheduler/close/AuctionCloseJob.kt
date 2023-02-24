package com.p2pbet.bet.auction.scheduler.close

import com.p2pbet.bet.auction.scheduler.close.dto.AuctionCloseJobData
import com.p2pbet.bet.auction.service.AuctionActionService
import com.p2pbet.bet.auction.service.AuctionBetService
import com.p2pbet.bet.auction.service.FreeAuctionActionService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.ExecutionAction
import com.p2pbet.bet.common.service.BIExecutionService
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
class AuctionCloseJob(
    val auctionBetService: AuctionBetService,
    val auctionActionService: AuctionActionService,
    val freeAuctionActionService: FreeAuctionActionService,
    val biExecutionService: BIExecutionService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(AuctionCloseJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }
        execute(jobData, context)
    }

    fun execute(jobData: AuctionCloseJobData, context: JobExecutionContext) {
        runCatching {
            logger.info { "Start auction close ${jobData.id}" }
            if (jobData.executionId == null) {
                initNewClose(jobData, context)
            } else {
                checkExecutionStatus(jobData, context)
            }
            logger.info { "End of auction close ${jobData.id}" }
        }.onFailure {
            logger.error("Execution of close error", it);
            if (context.isLastTimeTriggerFired()) {
                auctionBetService.markAuctionFailed(jobData.id, jobData.executionType, it.message)
                context.deleteCurrentJob()
            }
        }
    }

    private fun checkExecutionStatus(jobData: AuctionCloseJobData, context: JobExecutionContext) =
        when (biExecutionService.checkStatus(jobData.executionId!!, jobData.executionType)) {
            ExecutionStatus.PENDING,
            ExecutionStatus.SENT,
            -> {
                if (context.isLastTimeTriggerFired()) {
                    auctionBetService.markAuctionFailed(
                        id = jobData.id,
                        executionType = jobData.executionType,
                        errorMessage = "Async execution failed."
                    )
                    logger.error { "Close auction bet with failed status. Expiration timeout." }
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

    private fun initNewClose(jobData: AuctionCloseJobData, context: JobExecutionContext) {
        when (jobData.executionType) {
            BetExecutionType.FREE, BetExecutionType.FREE_QR -> initFreeNewClose(jobData, context)
            else -> initMainNewClose(jobData, context)
        }
    }

    private fun initFreeNewClose(jobData: AuctionCloseJobData, context: JobExecutionContext) {
        logger.info { "Init free new close call for auction ${jobData.id}" }

        freeAuctionActionService.callCloseAuction(jobData.id)

        logger.info { "Successfully closed free auction" }
        context.deleteCurrentJob()
    }

    private fun initMainNewClose(jobData: AuctionCloseJobData, context: JobExecutionContext) {
        logger.info { "Init main new close call for auction ${jobData.id}" }

        val executionId = biExecutionService.asyncWriteCall(
            executionAction = ExecutionAction.CLOSE_AUCTION_BET,
            betId = jobData.id
        ) {
            auctionActionService.callCloseAuction(
                betId = jobData.id,
                executionType = jobData.executionType
            )
        }
        jobData.executionId = executionId
        context.updateDataObject(jobData)
    }
}