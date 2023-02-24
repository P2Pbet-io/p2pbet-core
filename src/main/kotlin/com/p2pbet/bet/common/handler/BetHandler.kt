package com.p2pbet.bet.common.handler

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.queue.ContractType
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class BetHandler(
    val actionHandlers: List<ActionBetHandler>,
) {
    private val logger: KLogger = KotlinLogging.logger { }

    fun handler(abstractLog: AbstractLog, contractType: ContractType, executionType: BetExecutionType) {
        logger.debug { "Start of handling: ${abstractLog.transactionHash} - ${abstractLog::class.java}" }

        val handler = findHandler(abstractLog, contractType) ?: return

        handler.runCatching {
            handle(abstractLog, executionType)
        }.getOrElse {
            logger.error(
                "Exception during handling action ${abstractLog.transactionHash} - ${abstractLog::class.java}",
                it
            )
            return;
        }

        logger.debug { "End of handling: ${abstractLog.transactionHash} - ${abstractLog::class.java}" }
    }

    private fun findHandler(abstractLog: AbstractLog, contractType: ContractType): ActionBetHandler? =
        actionHandlers
            .firstOrNull() {
                it.isSupport(contractType, abstractLog.logType)
            }
            ?.apply {
                logger.debug { "Found ${this::class.java} handler" }
            }
            ?: run {
                logger.error { "No handler found by $contractType and ${abstractLog.logType}" }
                null
            }
}