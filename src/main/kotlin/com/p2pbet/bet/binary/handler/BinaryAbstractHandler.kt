package com.p2pbet.bet.binary.handler

import com.p2pbet.bet.common.handler.ActionBetHandler
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.messaging.model.queue.LogEnumMapper

abstract class BinaryAbstractHandler(
    val logType: LogEnumMapper
) : ActionBetHandler {

    override fun isSupport(contractType: ContractType, logType: LogEnumMapper): Boolean =
        contractType == ContractType.BINARY && logType == this.logType
}