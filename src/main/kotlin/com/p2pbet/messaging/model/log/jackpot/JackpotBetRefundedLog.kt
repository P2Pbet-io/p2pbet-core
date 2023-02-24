package com.p2pbet.messaging.model.log.jackpot

import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.annotation.AddressTopic
import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class JackpotBetRefundedLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:NumberValue(0)
    val betId: BigInteger,
    @field:AddressTopic(0)
    val clientAddress: String,
    @field:NumberValue(1)
    val mainTokenRefunded: BigInteger
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)