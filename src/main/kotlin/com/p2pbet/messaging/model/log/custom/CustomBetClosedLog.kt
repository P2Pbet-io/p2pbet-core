package com.p2pbet.messaging.model.log.custom

import com.p2pbet.messaging.model.log.AbstractLog

import com.p2pbet.messaging.model.log.annotation.BooleanValue
import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.log.annotation.StringValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class CustomBetClosedLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:NumberValue(0)
    val betId: BigInteger,
    @field:StringValue(1)
    val finalValue: String,
    @field:BooleanValue(2)
    val targetSideWon: Boolean
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)