package com.p2pbet.notification.schedule.dto

import com.p2pbet.messaging.notification.BetType

data class LockTimeNotificationJobData(
    val betId: Long,
    val betType: BetType,
)

