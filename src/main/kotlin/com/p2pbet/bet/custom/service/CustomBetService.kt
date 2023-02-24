package com.p2pbet.bet.custom.service

import com.p2pbet.bet.common.entity.BaseBetEntity
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.BetStatus
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.bet.custom.entity.CustomP2PBetEntity
import com.p2pbet.bet.custom.repository.CustomJoinRepository
import com.p2pbet.bet.custom.repository.CustomP2PBetRepository
import com.p2pbet.messaging.model.log.custom.CustomBetClosedLog
import com.p2pbet.messaging.model.log.custom.CustomBetCreatedLog
import com.p2pbet.messaging.notification.BetResultNotification
import com.p2pbet.messaging.notification.BetType
import com.p2pbet.messaging.notification.CreateBetNotification
import com.p2pbet.notification.NotificationSender
import com.p2pbet.messaging.notification.NotificationType
import com.p2pbet.notification.schedule.NotificationSchedulerService
import com.p2pbet.p2pevent.service.EventService
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class CustomBetService(
    val customP2PBetRepository: CustomP2PBetRepository,
    val eventService: EventService,
    val customBetSchedulerService: CustomBetSchedulerService,
    val notificationSender: NotificationSender,
    val customJoinRepository: CustomJoinRepository,
    val notificationSchedulerService: NotificationSchedulerService,
) {
    private val logger: KLogger = KotlinLogging.logger { }


    fun onCreate(createLog: CustomBetCreatedLog, executionType: BetExecutionType) = CustomP2PBetEntity(
        betId = createLog.id.longValueExact(),
        targetValue = BigDecimal(createLog.targetValue).setScale(2, RoundingMode.HALF_DOWN).toString(),
        targetSide = createLog.targetSide,
        coefficient = BigDecimal(createLog.coefficient).setScale(
            9,
            RoundingMode.HALF_DOWN
        ) / (BigDecimal(10).pow(9)).setScale(9, RoundingMode.HALF_DOWN),
        creator = createLog.creator,
        hidden = createLog.hidden,
        baseEvent = eventService.getOne(
            id = UUID.fromString(createLog.eventId)
        )
    ).also {
        notificationSender.send(
            CreateBetNotification(
                userId = it.creator,
                type = NotificationType.CREATE_BET,
                betId = it.betId,
                betType = BetType.CUSTOM,
            )
        )
    }.apply {
            baseBet = BaseBetEntity()
                .apply {
                    lockDate = LocalDateTime.ofEpochSecond(createLog.lockTime.longValueExact(), 0, ZoneOffset.UTC)
                    expirationDate =
                        LocalDateTime.ofEpochSecond(createLog.expirationTime.longValueExact(), 0, ZoneOffset.UTC)
                    createdTxHash = createLog.transactionHash
                    createdBlockNumber = createLog.blockNumber
                    this.executionType = executionType
                }
        }
        .apply(customP2PBetRepository::save)
        .also { logger.info { "Custom bet with id ${it.betId} created. Expiration time: ${it.baseBet.expirationDate}" } }
        .apply(customBetSchedulerService::scheduleCustomExecutionJob)
        .apply {
            notificationSchedulerService.scheduleLockTimeExpirationJob(
                betId = betId,
                betType = BetType.CUSTOM,
                lockTime = baseBet.lockDate.minusMinutes(5)
            )

            notificationSchedulerService.scheduleLockTimeExpirationJob(
                betId = betId,
                betType = BetType.CUSTOM,
                lockTime = baseBet.lockDate.minusMinutes(60)
            )
        }

    fun getCustomBet(id: Long, executionType: BetExecutionType) =
        customP2PBetRepository.findFirstByBetIdAndBaseBetExecutionType(id, executionType)

    @Transactional
    fun onExpirationCustomBetMissed(customP2PBetEntity: CustomP2PBetEntity) = customP2PBetEntity
        .apply {
            status = BetStatus.SKIPPED_CLOSING
        }
        .apply(customP2PBetRepository::save)
        .also { logger.info { "Custom bet with id ${it.betId} skipped. No joins found" } }

    @Transactional
    fun onExpirationCustomBetSuccess(customP2PBetEntity: CustomP2PBetEntity) = customP2PBetEntity
        .apply {
            status = BetStatus.PENDING_CLOSE
        }
        .apply(customP2PBetRepository::save)
        .apply(customBetSchedulerService::scheduleCustomCloseJob)

    @Transactional
    fun onCloseBet(closeLog: CustomBetClosedLog, executionType: BetExecutionType) =
        getCustomBet(closeLog.betId.longValueExact(), executionType)
            .apply {
                status = BetStatus.CLOSED
                baseBet.closedTxHash = closeLog.transactionHash
                baseBet.closedBlockNumber = closeLog.blockNumber
            }
            .apply(customP2PBetRepository::save)
            .apply(customBetSchedulerService::scheduleCustomPostCloseJob)
            .also {
                customJoinRepository.findAllByCustomBetBetIdAndStatusIn(it.betId, setOf(JoinStatus.LOST, JoinStatus.WON))
                    .forEach {
                        join ->
                        notificationSender.send(
                            BetResultNotification(
                                betType = BetType.CUSTOM,
                                userId = join.client,
                                type = NotificationType.BET_RESULT,
                                betId = it.betId,
                                result = if (join.status == JoinStatus.LOST) BetResultNotification.Result.FAIL
                                         else BetResultNotification.Result.WON
                            )
                        )
                    }

            }
            .also { logger.info { "Custom bet with id ${it.betId} closed. Target side won: ${closeLog.targetSideWon}" } }


    @Transactional
    fun markCustomFailed(id: Long, executionType: BetExecutionType, errorMessage: String?) =
        getCustomBet(id, executionType)
            .apply {
                status = BetStatus.FAILED
                baseBet.errorMessage = errorMessage
            }
            .apply(customP2PBetRepository::save)

}
