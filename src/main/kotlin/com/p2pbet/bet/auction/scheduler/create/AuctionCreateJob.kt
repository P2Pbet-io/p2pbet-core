package com.p2pbet.bet.auction.scheduler.create

import com.p2pbet.bet.auction.scheduler.create.dto.AuctionCreateJobData
import com.p2pbet.bet.auction.service.AuctionActionService
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
import java.time.OffsetDateTime

@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
class AuctionCreateJob(
    val auctionActionService: AuctionActionService,
    val freeAuctionActionService: FreeAuctionActionService,
    val biExecutionService: BIExecutionService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(AuctionCreateJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }
        execute(jobData, context)
    }

    fun execute(jobData: AuctionCreateJobData, context: JobExecutionContext) {
        runCatching {
            logger.info { "Start auction creation with data: $jobData" }
            if (jobData.executionId == null) {
                initNewCreate(jobData, context)
            } else {
                checkExecutionStatus(jobData, context)
            }
            logger.info { "End of auction creation with data: $jobData" }
        }.onFailure {
            logger.error("Execution of close error", it);
            if (context.isLastTimeTriggerFired()) {
                context.deleteCurrentJob()
            }
        }
    }

    private fun checkExecutionStatus(jobData: AuctionCreateJobData, context: JobExecutionContext) =
        when (biExecutionService.checkStatus(jobData.executionId!!, jobData.executionType)) {
            ExecutionStatus.PENDING,
            ExecutionStatus.SENT,
            -> {
                logger.info { "Caught waiting status." }
            }

            ExecutionStatus.CONFIRMED -> {
                logger.info { "Caught successful close auction write call." }
                context.deleteCurrentJob()
            }

            ExecutionStatus.FAILED -> {
                logger.info { "Failed during auction bet creation with data: $jobData" }
                context.deleteCurrentJob()
            }
        }

    private fun initNewCreate(jobData: AuctionCreateJobData, context: JobExecutionContext) {
        when (jobData.executionType) {
            BetExecutionType.FREE, BetExecutionType.FREE_QR -> initFreeNewCreate(jobData, context)
            else -> initMainNewCreate(jobData, context)
        }
    }

    private fun initFreeNewCreate(jobData: AuctionCreateJobData, context: JobExecutionContext) {
        logger.info { "Init new create call for free auction" }

        freeAuctionActionService.callCreateAuction(
            eventId = jobData.eventId.toString(),
            lockTime = OffsetDateTime.now().plusSeconds(jobData.lockPeriod),
            expirationTime = OffsetDateTime.now().plusSeconds(jobData.lockPeriod + jobData.expirationPeriod),
            requestAmount = jobData.requestAmount
        )

        logger.info { "Successfully created free auction" }
        context.deleteCurrentJob()
    }

    private fun initMainNewCreate(jobData: AuctionCreateJobData, context: JobExecutionContext) {
        logger.info { "Init new create call for auction" }

        val executionId = biExecutionService.asyncWriteCall(
            executionAction = ExecutionAction.CREATE_AUCTION_BET,
            betId = null
        ) {
            auctionActionService.callCreateAuction(
                eventId = jobData.eventId.toString(),
                lockTime = OffsetDateTime.now().plusSeconds(jobData.lockPeriod),
                expirationTime = OffsetDateTime.now().plusSeconds(jobData.lockPeriod + jobData.expirationPeriod),
                requestAmount = jobData.requestAmount,
                executionType = jobData.executionType,
            )
        }
        jobData.executionId = executionId
        context.updateDataObject(jobData)
    }
}
