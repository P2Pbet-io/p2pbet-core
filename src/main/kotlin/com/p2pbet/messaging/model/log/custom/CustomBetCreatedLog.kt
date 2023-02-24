package com.p2pbet.messaging.model.log.custom

import com.p2pbet.messaging.model.log.AbstractLog

import com.p2pbet.messaging.model.log.annotation.AddressTopic
import com.p2pbet.messaging.model.log.annotation.BooleanValue
import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.log.annotation.StringValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class CustomBetCreatedLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:NumberValue(0)
    val id: BigInteger,
    @field:StringValue(1)
    val eventId: String,
    @field:BooleanValue(2)
    val hidden: Boolean,
    @field:NumberValue(3)
    val lockTime: BigInteger,
    @field:NumberValue(4)
    val expirationTime: BigInteger,
    @field:StringValue(5)
    val targetValue: String,
    @field:BooleanValue(6)
    val targetSide: Boolean,
    @field:NumberValue(7)
    val coefficient: BigInteger,
    @field:AddressTopic(0)
    val creator: String,
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)