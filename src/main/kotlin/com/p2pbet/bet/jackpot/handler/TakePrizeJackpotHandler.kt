package com.p2pbet.bet.jackpot.handler

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.jackpot.service.JackpotJoinService
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.jackpot.JackpotBetPrizeTakenLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class TakePrizeJackpotHandler(
    val jackpotJoinService: JackpotJoinService,
) : JackpotAbstractHandler(
    logType = LogEnumMapper.JACKPOT_PRIZE_TAKEN
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as JackpotBetPrizeTakenLog) {
            logger.info { "Start JACKPOT_PRIZE_TAKEN handler" }
            jackpotJoinService.onJackpotPrizeTaken(this, executionType)
        }
}