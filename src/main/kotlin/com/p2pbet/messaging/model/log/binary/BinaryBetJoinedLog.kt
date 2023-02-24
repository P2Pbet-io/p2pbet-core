package com.p2pbet.messaging.model.log.binary

import com.p2pbet.messaging.model.log.AbstractLog

import com.p2pbet.messaging.model.log.annotation.AddressTopic
import com.p2pbet.messaging.model.log.annotation.BooleanValue
import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class BinaryBetJoinedLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:BooleanValue(0)
    val side: Boolean,
    @field:NumberValue(1)
    val mainAmount: BigInteger,
    @field:AddressTopic(0)
    val client: String,
    @field:NumberValue(2)
    val betId: BigInteger,
    @field:NumberValue(3)
    val joinId: BigInteger,
    @field:NumberValue(4)
    val joinIdRef: BigInteger
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)