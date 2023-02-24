package com.p2pbet.configuration.quartz

import java.time.LocalDateTime

data class TriggerProperties(
    val repeatCount: Int = 0,
    val repeatIntervalInSeconds: Int = 0,
    val startAt: LocalDateTime? = null,
    val endAt: LocalDateTime? = null,
    val cronExpression: String? = null,
    val priority: Int = 1
)
