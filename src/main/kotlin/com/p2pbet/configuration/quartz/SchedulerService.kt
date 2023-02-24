package com.p2pbet.configuration.quartz

import com.fasterxml.jackson.databind.ObjectMapper
import com.p2pbet.configuration.quartz.QuartzSchedulerConstant.DATA
import com.p2pbet.configuration.quartz.QuartzSchedulerConstant.FIRE_COUNT
import com.p2pbet.configuration.quartz.QuartzSchedulerConstant.LOG_JOB_START
import mu.KLogger
import mu.KotlinLogging
import org.quartz.*
import org.quartz.CronScheduleBuilder.cronSchedule
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class SchedulerService(
    val scheduler: Scheduler,
    val objectMapper: ObjectMapper
) {
    private val logger: KLogger = KotlinLogging.logger { }


    @PostConstruct
    fun start() {
        scheduler.start()
    }

    @PreDestroy
    fun shutdown() = scheduler.shutdown()

    fun schedule(
        jobClass: Class<out Job>,
        name: String,
        group: String,
        data: Any,
        triggerProperties: TriggerProperties,
        logJobStart: Boolean = true
    ) {
        scheduler.scheduleJob(
            JobBuilder
                .newJob(jobClass)
                .withIdentity(name, group)
                .setJobData(createJobDataMap(data, logJobStart))
                .build(),
            createTrigger(name, group, triggerProperties)
        )
        logger.info { "Job Scheduled. Key:$group.$name, data:$data, $triggerProperties" }
    }

    fun deleteJob(name: String, group: String) {
        scheduler.deleteJob(JobKey(name, group))
        logger.info { "Job Deleted. Key:$group.$name" }
    }

    fun rescheduleJob(
        name: String,
        group: String,
        triggerProperties: TriggerProperties
    ) {
        val trigger = createTrigger(
            name,
            group,
            triggerProperties
        )
        scheduler.rescheduleJob(TriggerKey(name, group), trigger)
        logger.info { "Job Rescheduled. Key:$group.$name, $triggerProperties" }
    }

    private fun createTrigger(
        name: String,
        group: String,
        triggerProperties: TriggerProperties
    ): Trigger = with(triggerProperties) {

        validateTriggerProperties(triggerProperties)

        val triggerBuilder = TriggerBuilder
            .newTrigger()
            .withIdentity(name, group)
            .withPriority(triggerProperties.priority)

        cronExpression?.run {
            return triggerBuilder
                .withSchedule(cronSchedule(cronExpression))
                .build()
        }

        if (triggerProperties.startAt == null) {
            triggerBuilder.startNow()
        } else {
            triggerBuilder.startAt(convertToDate(triggerProperties.startAt))
        }

        if (triggerProperties.endAt != null) {
            triggerBuilder.endAt(convertToDate(triggerProperties.endAt))
        }

        return triggerBuilder
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(triggerProperties.repeatIntervalInSeconds)
                    .withRepeatCount(triggerProperties.repeatCount)
            ).build()
    }

    private fun validateTriggerProperties(triggerProperties: TriggerProperties) = with(triggerProperties) {
        cronExpression?.run {
            if (repeatCount != 0 || repeatIntervalInSeconds != 0 || startAt != null || endAt != null) {
                throw TriggerPropertiesValidationException(
                    "If parameter 'cronExpression' is set, then other parameters should have default values."
                )
            }
        }
        if (repeatIntervalInSeconds < 0) {
            throw TriggerPropertiesValidationException("Parameter 'repeatIntervalInSeconds' must be >= 0")
        }
    }

    private fun createJobDataMap(data: Any, logJobStart: Boolean) = JobDataMap()
        .apply {
            put(FIRE_COUNT, "0")
            put(LOG_JOB_START, logJobStart.toString())
            put(DATA, objectMapper.writeValueAsString(data))
        }

    fun convertToDate(date: LocalDateTime): Date = Timestamp.valueOf(date)

}