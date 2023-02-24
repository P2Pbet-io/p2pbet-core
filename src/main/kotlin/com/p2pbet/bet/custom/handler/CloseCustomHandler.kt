package com.p2pbet.bet.custom.handler

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.custom.service.CustomBetService
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.custom.CustomBetClosedLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CloseCustomHandler(
    val customBetService: CustomBetService
) : CustomAbstractHandler(
    logType = LogEnumMapper.CUSTOM_CLOSED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as CustomBetClosedLog) {
            logger.info { "Start CUSTOM_CLOSED handler" }
            customBetService.onCloseBet(this, executionType)
        }
}