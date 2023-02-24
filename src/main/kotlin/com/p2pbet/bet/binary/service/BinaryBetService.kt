package com.p2pbet.bet.binary.service

import com.p2pbet.bet.binary.entity.BinaryP2PBetEntity
import com.p2pbet.bet.binary.repository.BinaryJoinRepository
import com.p2pbet.bet.binary.repository.BinaryP2PBetRepository
import com.p2pbet.bet.common.entity.BaseBetEntity
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.BetStatus
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.messaging.model.log.binary.BinaryBetClosedLog
import com.p2pbet.messaging.model.log.binary.BinaryBetCreatedLog
import com.p2pbet.messaging.notification.BetResultNotification
import com.p2pbet.messaging.notification.BetType
import com.p2pbet.notification.NotificationSender
import com.p2pbet.messaging.notification.NotificationType
import com.p2pbet.notification.schedule.NotificationSchedulerService
import com.p2pbet.p2pevent.service.EventService
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class BinaryBetService(
    val binaryP2PBetRepository: BinaryP2PBetRepository,
    val binaryBetSchedulerService: BinaryBetSchedulerService,
    val eventService: EventService,
    val notificationSender: NotificationSender,
    val binaryJoinRepository: BinaryJoinRepository,
    val notificationSchedulerService: NotificationSchedulerService,
) {
    private val logger: KLogger = KotlinLogging.logger { }

    @Transactional
    fun creatingBinaryBet(eventId: UUID, lockPeriod: Long, expirationPeriod: Long, executionType: BetExecutionType) =
        binaryBetSchedulerService.scheduleBinaryCreateJob(
            eventId = eventId,
            lockPeriod = lockPeriod,
            expirationPeriod = expirationPeriod,
            executionType = executionType
        )

    @Transactional
    fun onCreateBinaryBet(createLog: BinaryBetCreatedLog, executionType: BetExecutionType) =
        BinaryP2PBetEntity(
            betId = createLog.id.longValueExact(),
            period = createLog.expirationTime.longValueExact() - createLog.lockTime.longValueExact(),
            baseEvent = eventService.getOne(
                id = UUID.fromString(createLog.eventId)
            )
        )
            .apply {
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
            .apply(binaryP2PBetRepository::save)
            .also { logger.info { "Binary bet with id ${it.betId} created. Expiration time: ${it.baseBet.expirationDate}" } }
            .apply(binaryBetSchedulerService::scheduleBinaryExpirationJob)
            .apply {
                notificationSchedulerService.scheduleLockTimeExpirationJob(
                    betId = betId,
                    betType = BetType.BINARY,
                    lockTime = baseBet.lockDate.minusMinutes(5)
                )
            }

    @Transactional
    fun onCloseBet(closeLog: BinaryBetClosedLog, executionType: BetExecutionType) =
        getBinaryBet(closeLog.betId.longValueExact(), executionType)
            .apply {
                status = BetStatus.CLOSED
                baseBet.closedTxHash = closeLog.transactionHash
                baseBet.closedBlockNumber = closeLog.blockNumber
            }
            .apply(binaryP2PBetRepository::save)
            .apply(binaryBetSchedulerService::scheduleBinaryPostCloseJob)
            .also {
                binaryJoinRepository.findAllByBinaryBetBetIdAndStatusIn(it.betId, setOf(JoinStatus.LOST, JoinStatus.WON))
                    .forEach {
                            join ->
                        notificationSender.send(
                            BetResultNotification(
                                betType = BetType.BINARY,
                                userId = join.client,
                                type = NotificationType.BET_RESULT,
                                betId = it.betId,
                                result = if (join.status == JoinStatus.LOST) BetResultNotification.Result.FAIL
                                else BetResultNotification.Result.WON
                            )
                        )
                    }

            }
            .also { logger.info { "Binary bet with id ${it.betId} closed. Side won: ${closeLog.sideWon}" } }


    @Transactional
    fun onExpirationBinaryBetMissed(binaryP2PBetEntity: BinaryP2PBetEntity) = binaryP2PBetEntity
        .apply {
            status = BetStatus.SKIPPED_CLOSING
        }
        .apply(binaryP2PBetRepository::save)
        .also { logger.info { "Binary bet with id ${it.betId} skipped. No joins found" } }

    @Transactional
    fun onExpirationBinaryBetSuccess(binaryP2PBetEntity: BinaryP2PBetEntity) = binaryP2PBetEntity
        .apply {
            status = BetStatus.PENDING_CLOSE
        }
        .apply(binaryP2PBetRepository::save)
        .apply(binaryBetSchedulerService::scheduleBinaryCloseJob)

    fun getBinaryBet(id: Long, executionType: BetExecutionType) =
        binaryP2PBetRepository.findFirstByBetIdAndBaseBetExecutionType(id, executionType)

    @Transactional
    fun markBinaryFailed(id: Long, executionType: BetExecutionType, errorMessage: String?) =
        getBinaryBet(id, executionType)
            .apply {
                status = BetStatus.FAILED
                baseBet.errorMessage = errorMessage
            }
            .apply(binaryP2PBetRepository::save)


}
