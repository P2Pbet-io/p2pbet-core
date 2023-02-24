package com.p2pbet.messaging.model.log.auction

import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.annotation.ListNumberValue

import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.log.annotation.StringValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class AuctionBetClosedLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:NumberValue(0)
    val betId: BigInteger,
    @field:StringValue(1)
    val finalValue: String,
    @field:ListNumberValue(2)
    val joinIdsWon: List<BigInteger>,
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)