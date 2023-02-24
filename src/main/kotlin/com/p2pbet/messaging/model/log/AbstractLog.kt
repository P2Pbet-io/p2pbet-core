package com.p2pbet.messaging.model.log

import com.p2pbet.messaging.model.queue.LogEnumMapper
import java.math.BigInteger

abstract class AbstractLog(
    open val contractAddress: String,
    open val blockNumber: BigInteger,
    open val transactionHash: String,
    open val logType: LogEnumMapper
)
