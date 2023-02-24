package com.p2pbet.bet.custom.handler

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.custom.service.CustomCreationBetService
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.custom.CustomBetCreatedLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CreateCustomHandler(
    val customCreationBetService: CustomCreationBetService,
) : CustomAbstractHandler(
    logType = LogEnumMapper.CUSTOM_CREATED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as CustomBetCreatedLog) {
            logger.info { "Start CUSTOM_CREATED handler" }
            customCreationBetService.onCreate(this, executionType)
        }
}