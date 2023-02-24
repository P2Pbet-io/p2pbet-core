package com.p2pbet.configuration.quartz

import com.p2pbet.configuration.quartz.QuartzSchedulerConstant.DATA
import com.p2pbet.configuration.quartz.QuartzSchedulerConstant.FIRE_COUNT
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

object JobExecutionContextHelper {
    lateinit var schedulerService: SchedulerService

    fun <T> JobExecutionContext.getDataObject(dataClass: Class<T>): T =
        runCatching {
            schedulerService.objectMapper.readValue(
                this.jobDetail.jobDataMap.getString(DATA),
                dataClass
            )
        }.getOrElse {
            throw RuntimeException(
                "Can not get and convert DATA from JobExecutionContext.jobDetail.jobDataMap. $it",
                it
            )
        }

    fun JobExecutionContext.updateDataObject(data: Any) {
        this.jobDetail.jobDataMap[DATA] = schedulerService.objectMapper.writeValueAsString(data)
    }

    fun JobExecutionContext.deleteCurrentJob() {
        schedulerService.deleteJob(jobDetail.key.name, jobDetail.key.group)
    }

    fun JobExecutionContext.rescheduleCurrentJob(triggerProperties: TriggerProperties) =
        schedulerService.rescheduleJob(
            jobDetail.key.name,
            jobDetail.key.group,
            triggerProperties
        )

    fun JobExecutionContext.getRepeatCount(): Int = jobDetail.jobDataMap.getString(FIRE_COUNT).toInt()

    fun JobExecutionContext.isLastTimeTriggerFired(): Boolean = nextFireTime == null
}

@Component
class JobExecutionContextHelperInjector(
    protected val schedulerService: SchedulerService
) {
    @PostConstruct
    fun inject() {
        JobExecutionContextHelper.schedulerService = schedulerService
    }
}