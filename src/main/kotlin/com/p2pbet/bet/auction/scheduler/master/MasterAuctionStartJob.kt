package com.p2pbet.bet.auction.scheduler.master

import com.p2pbet.bet.auction.scheduler.master.dto.MasterAuctionStartJobData
import com.p2pbet.bet.auction.service.AuctionBetService
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.getDataObject
import mu.KLogger
import mu.KotlinLogging
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component

@Component
@DisallowConcurrentExecution
class MasterAuctionStartJob(
    val auctionBetService: AuctionBetService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(MasterAuctionStartJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }
        auctionBetService.creatingAuctionBet(
            eventId = jobData.eventId,
            lockPeriod = jobData.lockPeriod,
            expirationPeriod = jobData.expirationPeriod,
            requestAmount = jobData.requestAmount,
            executionType = jobData.executionType
        )
    }
}