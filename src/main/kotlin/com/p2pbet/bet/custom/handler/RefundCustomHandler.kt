package com.p2pbet.bet.custom.handler

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.custom.service.CustomJoinService
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.custom.CustomBetRefundedLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class RefundCustomHandler(
    val customJoinService: CustomJoinService,
) : CustomAbstractHandler(
    logType = LogEnumMapper.CUSTOM_REFUNDED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as CustomBetRefundedLog) {
            logger.info { "Start CUSTOM_REFUNDED handler" }
            customJoinService.onCustomRefund(this, executionType)
        }
}