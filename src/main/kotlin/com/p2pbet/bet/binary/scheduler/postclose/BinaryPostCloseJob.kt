package com.p2pbet.bet.binary.scheduler.postclose

import com.p2pbet.bet.binary.rest.model.BinaryBetResponse
import com.p2pbet.bet.binary.rest.model.BinaryJoinResponse
import com.p2pbet.bet.binary.rest.model.BinaryJoinsPageableWithFilterRequest
import com.p2pbet.bet.binary.scheduler.postclose.dto.BinaryPostCloseJobData
import com.p2pbet.bet.binary.service.BinaryBetFilterService
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
class BinaryPostCloseJob(
    val binaryBetFilterService: BinaryBetFilterService,
    val clientBetJoinService: ClientBetJoinService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }
    private val size = 100;
    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(BinaryPostCloseJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }
        execute(jobData, context)

        context.deleteCurrentJob()
    }

    fun execute(jobData: BinaryPostCloseJobData, context: JobExecutionContext) {
        val resultJoins: MutableMap<String, MutableList<BinaryJoinResponse>> = HashMap()

        var request = jobData.prepareRequest(0, size, jobData.id, jobData.executionType)
        var pageResponse = binaryBetFilterService.getBinaryJoinsWithFilters(request);

        for (i in 0 until pageResponse.totalPages) {
            request = jobData.prepareRequest(i, size, jobData.id, jobData.executionType)
            pageResponse = binaryBetFilterService.getBinaryJoinsWithFilters(request);
            pageResponse.result.forEach {
                resultJoins[it.client] ?: run {
                    resultJoins[it.client] = mutableListOf()
                }
                resultJoins[it.client]!!.add(it)
            }
        }

        resultJoins.processResult(jobData.id, jobData.executionType)
    }

    fun Map<String, List<BinaryJoinResponse>>.processResult(betId: Long, executionType: BetExecutionType) =
        with(binaryBetFilterService.getBinaryBet(betId, executionType)) {
            this@processResult.forEach { entry ->
                val wonAmount = entry.value.foldRight(BigDecimal.ZERO) { binaryJoinResponse, acc ->
                    acc + evaluatePrize(binaryJoinResponse)
                }
                clientBetJoinService.onClose(
                    betId = betId,
                    type = ContractType.BINARY,
                    clientAddress = entry.key,
                    wonAmount = wonAmount,
                    executionType = executionType
                )
            }
        }

    fun BinaryBetResponse.evaluatePrize(join: BinaryJoinResponse): BigDecimal =
        if (join.status in listOf(JoinStatus.WON, JoinStatus.PRIZE_TAKEN)) {
            join.joinAmount * evaluateCoefficient(join)
        } else {
            BigDecimal.ZERO
        }

    fun BinaryBetResponse.evaluateCoefficient(join: BinaryJoinResponse): BigDecimal =
        kotlin.runCatching {
            if (join.side) {
                (this.leftPool + this.rightPool) / this.leftPool
            } else {
                (this.leftPool + this.rightPool) / this.rightPool
            }
        }.getOrElse {
            logger.error("Error during evaluation coefficient", it)
            BigDecimal(1)
        }

    fun BinaryPostCloseJobData.prepareRequest(page: Int, size: Int, binaryBet: Long, executionType: BetExecutionType) =
        BinaryJoinsPageableWithFilterRequest(
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
            binaryBet = binaryBet,
            client = null,
            ids = null,
            executionType = executionType
        )
}