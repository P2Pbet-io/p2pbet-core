package com.p2pbet.messaging.model.log.common

import com.p2pbet.messaging.model.log.AbstractLog

import com.p2pbet.messaging.model.log.annotation.NumberValue
import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

class CompanyFeeChangedLog(
    override val contractAddress: String,
    override val blockNumber: BigInteger,
    override val transactionHash: String,
    override val logType: LogEnumMapper,
    @field:NumberValue(0)
    val previousCompanyFee: BigInteger,
    @field:NumberValue(1)
    val newCompanyFee: BigInteger
) : AbstractLog(
    contractAddress = contractAddress,
    blockNumber = blockNumber,
    transactionHash = transactionHash,
    logType = logType
)