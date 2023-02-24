package com.p2pbet.bet.binary.handler

import com.p2pbet.bet.binary.service.BinaryJoinService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.binary.BinaryBetRefundedLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class RefundBinaryHandler(
    val binaryJoinService: BinaryJoinService,
) : BinaryAbstractHandler(
    logType = LogEnumMapper.BINARY_REFUNDED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as BinaryBetRefundedLog) {
            logger.info { "Start BINARY_REFUNDED handler" }
            binaryJoinService.onBinaryRefund(action, executionType)
        }
}