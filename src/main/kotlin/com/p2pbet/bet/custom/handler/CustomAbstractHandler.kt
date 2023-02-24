package com.p2pbet.bet.custom.handler

import com.p2pbet.bet.common.handler.ActionBetHandler
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.messaging.model.queue.LogEnumMapper

abstract class CustomAbstractHandler(
    val logType: LogEnumMapper
) : ActionBetHandler {

    override fun isSupport(contractType: ContractType, logType: LogEnumMapper): Boolean =
        contractType == ContractType.CUSTOM && logType == this.logType
}