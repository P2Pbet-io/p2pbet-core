package com.p2pbet.client.bi

import com.p2pbet.bet.common.entity.enums.BetExecutionType

abstract class AbstractBlockchainIntegrationClient<T>(
    private val bscWriteApi: T,
    private val polygonWriteApi: T,
    private val avalancheWriteApi: T,
    private val tronWriteApi: T,
) {

    fun getApi(executionType: BetExecutionType) = when (executionType) {
        BetExecutionType.BSC -> bscWriteApi
        BetExecutionType.POLYGON -> polygonWriteApi
        BetExecutionType.TRON -> tronWriteApi
        BetExecutionType.AVALANCHE -> avalancheWriteApi
        else -> throw NotImplementedError("Not supported type: $executionType")
    }
}