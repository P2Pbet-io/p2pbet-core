package com.p2pbet.bet.common.handler

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.messaging.model.queue.LogEnumMapper

interface ActionBetHandler {
    fun isSupport(contractType: ContractType, logType: LogEnumMapper): Boolean

    fun handle(action: AbstractLog, executionType: BetExecutionType)
}