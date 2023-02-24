package com.p2pbet.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.queue.LogEnumMapper
import com.p2pbet.messaging.model.queue.LogQueueMessageDTO

abstract class AbstractQueueHandler(
    open val objectMapper: ObjectMapper,
) {
    protected fun extractMessage(message: String): AbstractLog {
        val logQueueMessageDTO = objectMapper.readValue(message, LogQueueMessageDTO::class.java)

        val logMapperClass = LogEnumMapper.extractByLogName(logQueueMessageDTO.logName)

        return objectMapper.convertValue(logQueueMessageDTO.payload, logMapperClass.extendedClass.java)
    }
}