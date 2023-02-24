package com.p2pbet.bet.jackpot.handler

import com.p2pbet.bet.common.handler.ActionBetHandler
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.messaging.model.queue.LogEnumMapper

abstract class JackpotAbstractHandler(
    val logType: LogEnumMapper,
) : ActionBetHandler {

    override fun isSupport(contractType: ContractType, logType: LogEnumMapper): Boolean =
        contractType == ContractType.JACKPOT && logType == this.logType
}