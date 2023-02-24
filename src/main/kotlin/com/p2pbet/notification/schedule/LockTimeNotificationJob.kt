package com.p2pbet.notification.schedule

import com.p2pbet.bet.auction.repository.AuctionJoinRepository
import com.p2pbet.bet.binary.repository.BinaryJoinRepository
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.bet.custom.repository.CustomJoinRepository
import com.p2pbet.bet.jackpot.entity.enums.JackpotJoinStatus
import com.p2pbet.bet.jackpot.repository.JackpotJoinRepository
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.deleteCurrentJob
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.getDataObject
import com.p2pbet.configuration.quartz.JobExecutionContextHelper.isLastTimeTriggerFired
import com.p2pbet.messaging.notification.BetType
import com.p2pbet.messaging.notification.LockTimeNotification
import com.p2pbet.messaging.notification.NotificationType
import com.p2pbet.notification.NotificationSender
import com.p2pbet.notification.schedule.dto.LockTimeNotificationJobData
import mu.KLogger
import mu.KotlinLogging
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.PersistJobDataAfterExecution
import org.springframework.stereotype.Component

@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
class LockTimeNotificationJob(
    val notificationSender: NotificationSender,
    val binaryJoinRepository: BinaryJoinRepository,
    val auctionJoinRepository: AuctionJoinRepository,
    val customJoinRepository: CustomJoinRepository,
    val jackpotJoinRepository: JackpotJoinRepository,
) : Job {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun execute(context: JobExecutionContext) {
        val jobData = runCatching { context.getDataObject(LockTimeNotificationJobData::class.java) }
            .getOrElse {
                logger.error(it.message, it)
                return
            }
        execute(jobData, context)
    }

    fun execute(jobData: LockTimeNotificationJobData, context: JobExecutionContext) {
        runCatching {
            logger.info { "Start sending locktime notification for: $jobData" }
            when (jobData.betType) {
                BetType.BINARY -> binaryJoinRepository.findAllByBinaryBetBetIdAndStatusIn(
                    betId = jobData.betId,
                    status = setOf(JoinStatus.JOINED)
                ).forEach {
                    sendNotification(
                        userId = it.client,
                        type = NotificationType.LOCK_TIME_EXPIRATION,
                        betId = jobData.betId,
                        betType = BetType.BINARY,
                    )
                }

                BetType.CUSTOM -> customJoinRepository.findAllByCustomBetBetIdAndStatusIn(
                    betId = jobData.betId,
                    status = setOf(JoinStatus.JOINED)
                ).forEach {
                    sendNotification(
                        userId = it.client,
                        type = NotificationType.LOCK_TIME_EXPIRATION,
                        betId = jobData.betId,
                        betType = BetType.CUSTOM,
                    )
                }

                BetType.AUCTION -> auctionJoinRepository.findAllByAuctionBetBetIdAndStatusIn(
                    betId = jobData.betId,
                    status = setOf(JoinStatus.JOINED)
                ).forEach {
                    sendNotification(
                        userId = it.client,
                        type = NotificationType.LOCK_TIME_EXPIRATION,
                        betId = jobData.betId,
                        betType = BetType.AUCTION,
                    )
                }

                BetType.JACKPOT -> jackpotJoinRepository.findAllByJackpotBetBetIdAndStatusIn(
                    betId = jobData.betId,
                    status = setOf(JackpotJoinStatus.JOINED)
                ).forEach {
                    sendNotification(
                        userId = it.client,
                        type = NotificationType.LOCK_TIME_EXPIRATION,
                        betId = jobData.betId,
                        betType = BetType.JACKPOT,
                    )
                }

            }
            logger.info { "End sending locktime notification for: $jobData" }
        }.onFailure {
            logger.error("Execution of sending locktime notification error", it);
            if (context.isLastTimeTriggerFired()) {
                context.deleteCurrentJob()
            }
        }
    }

    private fun sendNotification(
        userId: String,
        type: NotificationType,
        betId: Long,
        betType: BetType,
    ) =
        notificationSender.send(
            LockTimeNotification(
                userId = userId,
                type = type,
                betId = betId,
                betType = betType,
            )
        )
}
