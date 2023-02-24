package com.p2pbet.bet.custom.scheduler.postclose

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.bet.custom.rest.model.CustomBetResponse
import com.p2pbet.bet.custom.rest.model.CustomJoinResponse
import com.p2pbet.bet.custom.rest.model.CustomJoinsPageableWithFilterRequest
import com.p2pbet.bet.custom.scheduler.postclose.dto.CustomPostCloseJobData
import com.p2pbet.bet.custom.service.CustomBetFilterService
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
class CustomPostCloseJob(
    val customBetFilterService: CustomBetFilterService,
    val clientBetJoinService: ClientBetJoinService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }
    private val size = 100;
    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(CustomPostCloseJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }
        execute(jobData, context)

        context.deleteCurrentJob()
    }

    fun execute(jobData: CustomPostCloseJobData, context: JobExecutionContext) {
        val resultJoins: MutableMap<String, MutableList<CustomJoinResponse>> = HashMap()

        var request = jobData.prepareRequest(0, size, jobData.id, jobData.executionType)
        var pageResponse = customBetFilterService.getCustomJoinsWithFilters(request);

        for (i in 0 until pageResponse.totalPages) {
            request = jobData.prepareRequest(i, size, jobData.id, jobData.executionType)
            pageResponse = customBetFilterService.getCustomJoinsWithFilters(request);
            pageResponse.result.forEach {
                resultJoins[it.client] ?: run {
                    resultJoins[it.client] = mutableListOf()
                }
                resultJoins[it.client]!!.add(it)
            }
        }

        resultJoins.processResult(jobData.id, jobData.executionType)
    }

    fun Map<String, List<CustomJoinResponse>>.processResult(betId: Long, executionType: BetExecutionType) =
        with(customBetFilterService.getCustomBet(betId, executionType)) {
            this@processResult.forEach { entry ->
                val wonAmount = entry.value.foldRight(BigDecimal.ZERO) { customJoinResponse, acc ->
                    acc + evaluatePrize(customJoinResponse) + customJoinResponse.freeAmount
                }
                clientBetJoinService.onClose(
                    betId = betId,
                    type = ContractType.CUSTOM,
                    clientAddress = entry.key,
                    wonAmount = wonAmount,
                    executionType = executionType
                )
            }
        }

    fun CustomBetResponse.evaluatePrize(join: CustomJoinResponse): BigDecimal =
        if (join.status in listOf(JoinStatus.WON, JoinStatus.PRIZE_TAKEN)) {
            join.lockedAmount * evaluateCoefficient(join)
        } else {
            BigDecimal.ZERO
        }

    fun CustomBetResponse.evaluateCoefficient(join: CustomJoinResponse): BigDecimal =
        kotlin.runCatching {
            if (this.targetSide == join.side) {
                coefficient
            } else {
                coefficient / (coefficient - BigDecimal.ONE)
            }
        }.getOrElse {
            logger.error("Error during evaluation coefficient", it)
            BigDecimal(1)
        }

    fun CustomPostCloseJobData.prepareRequest(page: Int, size: Int, customBet: Long, executionType: BetExecutionType) =
        CustomJoinsPageableWithFilterRequest(
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
            customBet = customBet,
            client = null,
            ids = null,
            executionType = executionType
        )
}