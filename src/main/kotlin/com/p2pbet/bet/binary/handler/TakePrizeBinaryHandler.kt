package com.p2pbet.bet.binary.handler

import com.p2pbet.bet.binary.service.BinaryJoinService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.binary.BinaryBetPrizeTakenLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class TakePrizeBinaryHandler(
    val binaryJoinService: BinaryJoinService,
) : BinaryAbstractHandler(
    logType = LogEnumMapper.BINARY_PRIZE_TAKEN
) {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun handle(action: AbstractLog, executionType: BetExecutionType): Unit =
        with(action as BinaryBetPrizeTakenLog) {
            logger.info { "Start BINARY_PRIZE_TAKEN handler" }
            binaryJoinService.onBinaryPrizeTaken(this, executionType)
        }
}