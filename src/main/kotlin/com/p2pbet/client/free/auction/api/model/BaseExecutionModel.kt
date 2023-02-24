package com.p2pbet.client.free.auction.api.model

import com.p2pbet.messaging.model.queue.ContractType


abstract class BaseExecutionModel(
    val contractType: ContractType,
)