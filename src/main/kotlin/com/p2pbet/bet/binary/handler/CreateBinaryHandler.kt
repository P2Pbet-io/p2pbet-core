package com.p2pbet.bet.binary.handler

import com.p2pbet.bet.binary.service.BinaryBetService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.binary.BinaryBetCreatedLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CreateBinaryHandler(
    val binaryBetService: BinaryBetService
) : BinaryAbstractHandler(
    logType = LogEnumMapper.BINARY_CREATED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as BinaryBetCreatedLog) {
            logger.info { "Start BINARY_CREATED handler" }
            binaryBetService.onCreateBinaryBet(
                createLog = this,
                executionType = executionType
            )
        }
}