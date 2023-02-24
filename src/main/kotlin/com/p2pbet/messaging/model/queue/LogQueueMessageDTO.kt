package com.p2pbet.messaging.model.queue

data class LogQueueMessageDTO(
    val contractType: ContractType,
    val logName: String,
    val payload: Map<*, *>
)
