package com.p2pbet.bet.custom.handler

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.custom.service.CustomJoinService
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.custom.CustomBetPrizeTakenLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class TakePrizeCustomHandler(
    val customJoinService: CustomJoinService,
) : CustomAbstractHandler(
    logType = LogEnumMapper.CUSTOM_PRIZE_TAKEN
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as CustomBetPrizeTakenLog) {
            logger.info { "Start CUSTOM_PRIZE_TAKEN handler" }
            customJoinService.onCustomPrizeTaken(this, executionType)
        }
}