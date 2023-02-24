package com.p2pbet.messaging.model.log.common

import com.p2pbet.messaging.model.log.AbstractLog

import com.p2pbet.messaging.model.log.annotation.AddressTopic
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class TransferredCompanyLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:AddressTopic(0)
    val previousCompany: String,
    @field:AddressTopic(1)
    val newCompany: String,
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)