package com.p2pbet.bet.binary.scheduler.master

import com.p2pbet.bet.binary.scheduler.master.dto.MasterBinaryStartJobData
import com.p2pbet.bet.binary.service.BinaryBetService
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.getDataObject
import mu.KLogger
import mu.KotlinLogging
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component

@Component
@DisallowConcurrentExecution
class MasterBinaryStartJob(
    val binaryBetService: BinaryBetService,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(MasterBinaryStartJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }
        binaryBetService.creatingBinaryBet(
            eventId = jobData.eventId,
            lockPeriod = jobData.lockPeriod,
            expirationPeriod = jobData.expirationPeriod,
            executionType = jobData.executionType
        )
    }
}