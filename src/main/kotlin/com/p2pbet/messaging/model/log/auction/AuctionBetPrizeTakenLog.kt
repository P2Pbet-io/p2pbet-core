package com.p2pbet.messaging.model.log.auction

import com.p2pbet.messaging.model.log.AbstractLog

import com.p2pbet.messaging.model.log.annotation.AddressTopic
import com.p2pbet.messaging.model.log.annotation.BooleanValue
import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class AuctionBetPrizeTakenLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:NumberValue(0)
    val betId: BigInteger,
    @field:AddressTopic(0)
    val clientAddress: String,
    @field:NumberValue(1)
    val amount: BigInteger,
    @field:BooleanValue(2)
    val useAlterFee: Boolean,
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)