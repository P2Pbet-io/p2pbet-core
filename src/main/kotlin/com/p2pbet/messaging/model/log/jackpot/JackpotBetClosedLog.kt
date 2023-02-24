package com.p2pbet.messaging.model.log.jackpot

import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.log.annotation.StringValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class JackpotBetClosedLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:NumberValue(0)
    val betId: BigInteger,
    @field:StringValue(1)
    val finalValue: String,
    @field:NumberValue(2)
    val firstWonSize: BigInteger,
    @field:NumberValue(3)
    val secondWonSize: BigInteger,
    @field:NumberValue(4)
    val thirdWonSize: BigInteger,
    @field:NumberValue(5)
    val totalRaffled: BigInteger,
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)