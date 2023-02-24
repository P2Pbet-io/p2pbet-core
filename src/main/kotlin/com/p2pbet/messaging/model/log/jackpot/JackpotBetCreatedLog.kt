package com.p2pbet.messaging.model.log.jackpot

import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.annotation.AddressTopic
import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.log.annotation.StringValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class JackpotBetCreatedLog(
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
    val creator: String,
    @field:NumberValue(4)
    val startBank: BigInteger,
    @field:NumberValue(5)
    val requestAmount: BigInteger,
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)