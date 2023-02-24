package com.p2pbet.messaging.model.log.auction

import com.p2pbet.messaging.model.log.AbstractLog

import com.p2pbet.messaging.model.log.annotation.AddressTopic
import com.p2pbet.messaging.model.log.annotation.BooleanValue
import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.log.annotation.StringValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class AuctionBetJoinedLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:AddressTopic(0)
    val client: String,
    @field:NumberValue(0)
    val betId: BigInteger,
    @field:NumberValue(1)
    val joinId: BigInteger,
    @field:NumberValue(2)
    val joinIdRef: BigInteger,
    @field:StringValue(3)
    val targetValue: String
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)