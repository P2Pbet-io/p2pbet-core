package com.p2pbet.notification.schedule

import com.p2pbet.configuration.quartz.SchedulerService
import com.p2pbet.configuration.quartz.TriggerProperties
import com.p2pbet.messaging.notification.BetType
import com.p2pbet.notification.schedule.dto.LockTimeNotificationJobData
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class NotificationSchedulerService(
    val schedulerService: SchedulerService,
) {
    fun scheduleLockTimeExpirationJob(
        betId: Long,
        betType: BetType,
        lockTime: LocalDateTime
    ) =
        schedulerService.schedule(
            jobClass = LockTimeNotificationJob::class.java,
            name = "${LockTimeNotificationJob::class.java.simpleName}_${UUID.randomUUID()}",
            group = LockTimeNotificationJob::class.java.simpleName,
            data = LockTimeNotificationJobData(
                betId = betId,
                betType = betType,
            ),
            triggerProperties = TriggerProperties(
                repeatCount = 1,
                repeatIntervalInSeconds = 10,
                priority = 15,
                startAt = lockTime,
                endAt = lockTime.plusMinutes(3),
            )
        )
}
