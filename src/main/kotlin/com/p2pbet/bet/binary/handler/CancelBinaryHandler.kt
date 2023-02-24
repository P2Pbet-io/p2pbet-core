package com.p2pbet.bet.binary.handler

import com.p2pbet.bet.binary.service.BinaryJoinService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.binary.BinaryBetCanceledLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CancelBinaryHandler(
    val binaryJoinService: BinaryJoinService,
) : BinaryAbstractHandler(
    logType = LogEnumMapper.BINARY_CANCELED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as BinaryBetCanceledLog) {
            logger.info { "Start BINARY_CANCELED handler" }
            binaryJoinService.onBinaryCancel(this, executionType)
        }
}