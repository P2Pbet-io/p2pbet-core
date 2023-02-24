package com.p2pbet.messaging.model.log.binary

import com.p2pbet.messaging.model.log.AbstractLog

import com.p2pbet.messaging.model.log.annotation.BooleanValue
import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.log.annotation.StringValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class BinaryBetClosedLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:NumberValue(0)
    val betId: BigInteger,
    @field:StringValue(1)
    val lockedValue: String,
    @field:StringValue(2)
    val finalValue: String,
    @field:BooleanValue(3)
    val sideWon: Boolean
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)