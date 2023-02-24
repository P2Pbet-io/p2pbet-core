package com.p2pbet.notification

import com.fasterxml.jackson.databind.ObjectMapper
import com.p2pbet.configuration.ActiveMQConfiguration
import com.p2pbet.messaging.notification.Notification
import mu.KLogger
import mu.KotlinLogging
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component

@Component
class NotificationSender(
    private val objectMapper: ObjectMapper,
    private val jmsTemplate: JmsTemplate,
) {
    private val logger: KLogger by lazy { KotlinLogging.logger { } }
    fun send(notification: Notification) {
        jmsTemplate.send(ActiveMQConfiguration.NOTIFICATION_QUEUE) { session ->
            val message = session.createTextMessage()
            message.text = objectMapper.writeValueAsString(notification)
            logger.info { "Sending message with payload: ${message.text} to notification queue" }
            message
        }
    }
}
