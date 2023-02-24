package com.p2pbet.bet.jackpot.handler

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.jackpot.service.JackpotBetService
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.jackpot.JackpotBetCreatedLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CreateJackpotHandler(
    val jackpotBetService: JackpotBetService,
) : JackpotAbstractHandler(
    logType = LogEnumMapper.JACKPOT_CREATED
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as JackpotBetCreatedLog) {
            logger.info { "Start JACKPOT_CREATED handler" }
            jackpotBetService.onCreateJackpotBet(this, executionType)
        }
}