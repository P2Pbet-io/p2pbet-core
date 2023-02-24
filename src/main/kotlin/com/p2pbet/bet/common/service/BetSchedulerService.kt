package com.p2pbet.bet.common.service

import com.p2pbet.bet.auction.scheduler.master.MasterAuctionStartJob
import com.p2pbet.bet.auction.scheduler.master.dto.MasterAuctionStartJobData
import com.p2pbet.bet.binary.scheduler.master.MasterBinaryStartJob
import com.p2pbet.bet.binary.scheduler.master.dto.MasterBinaryStartJobData
import com.p2pbet.bet.common.entity.BetSchedulerEntity
import com.p2pbet.bet.common.repository.BetSchedulerRepository
import com.p2pbet.bet.jackpot.scheduler.master.MasterJackpotStartJob
import com.p2pbet.bet.jackpot.scheduler.master.dto.MasterJackpotStartJobData
import com.p2pbet.configuration.quartz.SchedulerService
import com.p2pbet.configuration.quartz.TriggerProperties
import com.p2pbet.messaging.model.queue.ContractType
import mu.KLogger
import mu.KotlinLogging
import org.quartz.Job
import org.springframework.stereotype.Service

@Service
class BetSchedulerService(
    val schedulerService: SchedulerService,
    val betSchedulerRepository: BetSchedulerRepository,
) {
    private val logger: KLogger = KotlinLogging.logger { }

    fun reschedule() {
        betSchedulerRepository.findAll().forEach { schedulerBet ->
            if (schedulerBet.archive) {
                runCatching {
                    delete(schedulerBet.getJobName(), schedulerBet.getGroupName())
                    logger.info { "Successfully deleted: ${schedulerBet.getJobName()}-${schedulerBet.getGroupName()}" }
                }.onFailure {
                    logger.info { "Already deleted: ${schedulerBet.getJobName()}-${schedulerBet.getGroupName()}" }
                }
            } else {
                startMaster(
                    name = schedulerBet.getJobName(),
                    group = schedulerBet.getGroupName(),
                    cronExpression = schedulerBet.cron,
                    masterStartJobData = schedulerBet.getMasterJobData(),
                    jobClass = schedulerBet.getMasterJob()
                )
            }
        }
    }

    private fun BetSchedulerEntity.getJobName() = "$betType-$betExecutionType-$id"
    private fun BetSchedulerEntity.getGroupName() = getMasterJob().simpleName
    private fun BetSchedulerEntity.getMasterJob(): Class<out Job> = when (betType) {
        ContractType.JACKPOT -> MasterJackpotStartJob::class.java
        ContractType.AUCTION -> MasterAuctionStartJob::class.java
        ContractType.BINARY -> MasterBinaryStartJob::class.java
        else -> throw NotImplementedError()
    }

    private fun BetSchedulerEntity.getMasterJobData(): Any = when (betType) {
        ContractType.JACKPOT -> MasterJackpotStartJobData(
            eventId = eventId,
            lockPeriod = lockPeriod,
            expirationPeriod = expirationPeriod,
            requestAmount = requestAmount!!,
            executionType = betExecutionType
        )

        ContractType.AUCTION -> MasterAuctionStartJobData(
            eventId = eventId,
            lockPeriod = lockPeriod,
            expirationPeriod = expirationPeriod,
            requestAmount = requestAmount!!,
            executionType = betExecutionType
        )

        ContractType.BINARY -> MasterBinaryStartJobData(
            eventId = eventId,
            lockPeriod = lockPeriod,
            expirationPeriod = expirationPeriod,
            executionType = betExecutionType
        )

        else -> throw NotImplementedError()
    }

    private fun startMaster(
        name: String,
        group: String,
        cronExpression: String,
        masterStartJobData: Any,
        jobClass: Class<out Job>,
    ) {
        runCatching {
            schedule(name, group, cronExpression, masterStartJobData, jobClass)
            logger.info { "New schedule: $name, $group, '$cronExpression', $jobClass" }
        }.onFailure {
            delete(name, group)
            schedule(name, group, cronExpression, masterStartJobData, jobClass)
            logger.info { "Reschedule: $name, $group, '$cronExpression', $jobClass" }
        }
    }

    private fun delete(name: String, group: String) =
        schedulerService.deleteJob(
            name,
            group
        )

    private fun schedule(
        name: String,
        group: String,
        cronExpression: String,
        masterStartJobData: Any,
        jobClass: Class<out Job>,
    ) = schedulerService.schedule(
        jobClass = jobClass,
        name = name,
        group = group,
        data = masterStartJobData,
        triggerProperties = TriggerProperties(
            cronExpression = cronExpression,
            priority = 15
        ),
        logJobStart = false
    )
}