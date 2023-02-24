package com.p2pbet.bet.jackpot.scheduler.postclose

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.jackpot.entity.enums.JackpotJoinStatus
import com.p2pbet.bet.jackpot.rest.model.JackpotBetResponse
import com.p2pbet.bet.jackpot.rest.model.JackpotJoinResponse
import com.p2pbet.bet.jackpot.rest.model.JackpotJoinsPageableWithFilterRequest
import com.p2pbet.bet.jackpot.scheduler.postclose.dto.JackpotPostCloseJobData
import com.p2pbet.bet.jackpot.service.JackpotBetFilterService
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.deleteCurrentJob
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.getDataObject
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.users.service.ClientBetJoinService
import com.p2pbet.util.filter.model.FilteredJackpotJoinStatusList
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
import java.math.RoundingMode

@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
class JackpotPostCloseJob(
    val jackpotBetFilterService: JackpotBetFilterService,
    val clientBetJoinService: ClientBetJoinService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }
    private val size = 100;
    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(JackpotPostCloseJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }
        execute(jobData, context)

        context.deleteCurrentJob()
    }

    fun execute(jobData: JackpotPostCloseJobData, context: JobExecutionContext) {
        val resultJoins: MutableMap<String, MutableList<JackpotJoinResponse>> = HashMap()

        var request = jobData.prepareRequest(0, size, jobData.id, jobData.executionType)
        var pageResponse = jackpotBetFilterService.getJackpotJoinsWithFilters(request);

        for (i in 0 until pageResponse.totalPages) {
            request = jobData.prepareRequest(i, size, jobData.id, jobData.executionType)
            pageResponse = jackpotBetFilterService.getJackpotJoinsWithFilters(request);
            pageResponse.result.forEach {
                resultJoins[it.client] ?: run {
                    resultJoins[it.client] = mutableListOf()
                }
                resultJoins[it.client]!!.add(it)
            }
        }

        resultJoins.processResult(jobData.id, jobData.executionType)
    }

    fun Map<String, List<JackpotJoinResponse>>.processResult(betId: Long, executionType: BetExecutionType) =
        with(jackpotBetFilterService.getJackpotBet(betId, executionType)) {
            this@processResult.forEach { entry ->
                val wonAmount = entry.value.foldRight(BigDecimal.ZERO) { jackpotJoinResponse, acc ->
                    acc + evaluatePrize(jackpotJoinResponse)
                }
                clientBetJoinService.onClose(
                    betId = betId,
                    type = ContractType.JACKPOT,
                    clientAddress = entry.key,
                    wonAmount = wonAmount,
                    executionType = executionType
                )
            }
        }

    fun JackpotBetResponse.evaluatePrize(join: JackpotJoinResponse): BigDecimal = with(getAtomicPart()) {
        when (join.status) {
            JackpotJoinStatus.WON_FIRST, JackpotJoinStatus.PRIZE_TAKEN_FIRST -> this
            JackpotJoinStatus.WON_SECOND, JackpotJoinStatus.PRIZE_TAKEN_SECOND -> this * BigDecimal(11)
            JackpotJoinStatus.WON_THIRD, JackpotJoinStatus.PRIZE_TAKEN_THIRD -> this * BigDecimal(111)
            else -> BigDecimal.ZERO
        }
    }

    fun JackpotBetResponse.getAtomicPart(): BigDecimal =
        if ((this.thirdWonSize!! * 100 +
                    this.secondWonSize!! * 10 +
                    this.firstWonSize!!) == 0L
        ) {
            BigDecimal.ZERO
        } else {
            this.totalRaffled!!.setScale(18, RoundingMode.HALF_DOWN) / (
                    BigDecimal(
                        this.thirdWonSize * 100 +
                                this.secondWonSize * 10 +
                                this.firstWonSize
                    ))
        }


    fun JackpotPostCloseJobData.prepareRequest(
        page: Int,
        size: Int,
        jackpotBet: Long,
        executionType: BetExecutionType,
    ) =
        JackpotJoinsPageableWithFilterRequest(
            page = page,
            size = size,
            sort = Sort(
                direction = Direction.DESC,
                property = "client"
            ),
            statusList = FilteredJackpotJoinStatusList(
                list = JackpotJoinStatus.values().filterNot { JackpotJoinStatus.CANCELED == it }
            ),
            jackpotBet = jackpotBet,
            client = null,
            ids = null,
            executionType = executionType
        )
}