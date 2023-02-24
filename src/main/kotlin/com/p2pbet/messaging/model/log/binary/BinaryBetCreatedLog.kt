package com.p2pbet.messaging.model.log.binary

import com.p2pbet.messaging.model.log.AbstractLog

import com.p2pbet.messaging.model.log.annotation.AddressTopic
import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.log.annotation.StringValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class BinaryBetCreatedLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:NumberValue(0)
    val id: BigInteger,
    @field:StringValue(1)
    val eventId: String,
    @field:NumberValue(2)
    val lockTime: BigInteger,
    @field:NumberValue(3)
    val expirationTime: BigInteger,
    @field:AddressTopic(0)
    val creator: String
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)