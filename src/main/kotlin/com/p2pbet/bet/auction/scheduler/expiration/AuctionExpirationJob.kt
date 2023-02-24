package com.p2pbet.bet.auction.scheduler.expiration

import com.p2pbet.bet.auction.scheduler.expiration.dto.AuctionExpirationJobData
import com.p2pbet.bet.auction.service.AuctionBetService
import com.p2pbet.bet.auction.service.AuctionJoinService
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
class AuctionExpirationJob(
    val auctionBetService: AuctionBetService,
    val auctionJoinService: AuctionJoinService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(AuctionExpirationJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }

        execute(jobData, context)
    }

    fun execute(jobData: AuctionExpirationJobData, context: JobExecutionContext) {
        runCatching {
            logger.info { "Start auction expiration ${jobData.id}" }
            auctionJoinService.onExpirationAuction(
                auctionId = jobData.id,
                executionType = jobData.executionType
            )
            logger.info { "End of auction expiration ${jobData.id}" }
            context.deleteCurrentJob()
        }.onFailure {
            logger.error("Execution of expiration auction error", it);
            if (context.isLastTimeTriggerFired()) {
                auctionBetService.markAuctionFailed(jobData.id, jobData.executionType, it.message)
                context.deleteCurrentJob()
            }
        }
    }
}