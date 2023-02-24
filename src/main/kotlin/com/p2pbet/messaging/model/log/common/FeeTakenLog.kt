package com.p2pbet.messaging.model.log.common

import com.p2pbet.messaging.model.log.AbstractLog

import com.p2pbet.messaging.model.log.annotation.AddressTopic
import com.p2pbet.messaging.model.log.annotation.BooleanValue
import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class FeeTakenLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:NumberValue(0)
    val amount: BigInteger,
    @field:AddressTopic(0)
    val targetAddress: String,
    @field:BooleanValue(1)
    val isAlternative: Boolean,
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)