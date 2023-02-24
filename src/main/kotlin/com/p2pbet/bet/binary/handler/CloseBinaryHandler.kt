package com.p2pbet.bet.binary.handler

import com.p2pbet.bet.binary.service.BinaryBetService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.binary.BinaryBetClosedLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CloseBinaryHandler(
    val binaryBetService: BinaryBetService
) : BinaryAbstractHandler(
    logType = LogEnumMapper.BINARY_CLOSED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as BinaryBetClosedLog) {
            logger.info { "Start BINARY_CREATED handler" }
            binaryBetService.onCloseBet(
                closeLog = this,
                executionType = executionType
            )
        }
}