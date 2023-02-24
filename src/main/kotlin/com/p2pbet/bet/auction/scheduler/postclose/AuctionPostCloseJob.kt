package com.p2pbet.bet.auction.scheduler.postclose

import com.p2pbet.bet.auction.rest.model.AuctionJoinResponse
import com.p2pbet.bet.auction.rest.model.AuctionJoinsPageableWithFilterRequest
import com.p2pbet.bet.auction.scheduler.postclose.dto.AuctionPostCloseJobData
import com.p2pbet.bet.auction.service.AuctionBetFilterService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.deleteCurrentJob
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.getDataObject
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.users.service.ClientBetJoinService
import com.p2pbet.util.filter.model.FilteredJoinStatusList
import com.p2pbet.util.model.Direction
import com.p2pbet.util.model.Sort
import mu.KLogger
import mu.KotlinLogging
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.PersistJobDataAfterExecution
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
class AuctionPostCloseJob(
    val auctionBetFilterService: AuctionBetFilterService,
    val clientBetJoinService: ClientBetJoinService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }
    private val size = 100;
    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(AuctionPostCloseJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }
        execute(jobData, context)

        context.deleteCurrentJob()
    }

    fun execute(jobData: AuctionPostCloseJobData, context: JobExecutionContext) {
        val resultJoins: MutableMap<String, MutableList<AuctionJoinResponse>> = HashMap()

        var request = jobData.prepareRequest(0, size, jobData.id, jobData.executionType)
        var pageResponse = auctionBetFilterService.getAuctionJoinsWithFilters(request);

        for (i in 0 until pageResponse.totalPages) {
            request = jobData.prepareRequest(i, size, jobData.id, jobData.executionType)
            pageResponse = auctionBetFilterService.getAuctionJoinsWithFilters(request);
            pageResponse.result.forEach {
                resultJoins[it.client] ?: run {
                    resultJoins[it.client] = mutableListOf()
                }
                resultJoins[it.client]!!.add(it)
            }
        }

        resultJoins.processResult(jobData.id, jobData.executionType)
    }

    fun Map<String, List<AuctionJoinResponse>>.processResult(betId: Long, executionType: BetExecutionType) =
        with(auctionBetFilterService.getAuctionBet(betId, executionType)) {
            this@processResult.forEach { entry ->
                val lost = entry.value.none { join ->
                    join.status in listOf(JoinStatus.WON, JoinStatus.PRIZE_TAKEN)
                }
                clientBetJoinService.onClose(
                    betId = betId,
                    type = ContractType.AUCTION,
                    clientAddress = entry.key,
                    wonAmount = if (lost) {
                        BigDecimal.ZERO
                    } else {
                        this.totalPool
                    },
                    executionType = executionType
                )
            }
        }

    fun AuctionPostCloseJobData.prepareRequest(page: Int, size: Int, auctionId: Long, executionType: BetExecutionType) =
        AuctionJoinsPageableWithFilterRequest(
            page = page,
            size = size,
            sort = Sort(
                direction = Direction.DESC,
                property = "client"
            ),
            statusList = FilteredJoinStatusList(
                list = listOf(
                    JoinStatus.JOINED,
                    JoinStatus.PRIZE_TAKEN,
                    JoinStatus.WON,
                    JoinStatus.REFUNDED,
                    JoinStatus.LOST
                )
            ),
            auctionBet = auctionId,
            client = null,
            freeMode = null,
            ids = null,
            periodFilter = null,
            executionType = executionType
        )
}