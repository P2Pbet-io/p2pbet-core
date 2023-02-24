package com.p2pbet.bet.custom.handler

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.custom.service.CustomMatchingService
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.custom.CustomBetJoinedLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class JoinCustomHandler(
    val customMatchingService: CustomMatchingService,
) : CustomAbstractHandler(
    logType = LogEnumMapper.CUSTOM_JOINED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as CustomBetJoinedLog) {
            logger.info { "Start CUSTOM_JOINED handler" }
            customMatchingService.onCustomJoin(this, executionType)
        }
}