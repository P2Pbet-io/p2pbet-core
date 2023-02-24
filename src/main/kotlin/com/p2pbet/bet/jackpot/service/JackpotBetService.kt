package com.p2pbet.bet.jackpot.service

import com.p2pbet.bet.common.entity.BaseBetEntity
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.BetStatus
import com.p2pbet.bet.jackpot.entity.JackpotP2PBetEntity
import com.p2pbet.bet.jackpot.entity.enums.JackpotJoinStatus
import com.p2pbet.bet.jackpot.repository.JackpotJoinRepository
import com.p2pbet.bet.jackpot.repository.JackpotP2PBetRepository
import com.p2pbet.messaging.model.log.jackpot.JackpotBetClosedLog
import com.p2pbet.messaging.model.log.jackpot.JackpotBetCreatedLog
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
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class JackpotBetService(
    val jackpotP2PBetRepository: JackpotP2PBetRepository,
    val jackpotBetSchedulerService: JackpotBetSchedulerService,
    val eventService: EventService,
    val jackpotJoinRepository: JackpotJoinRepository,
    val notificationSender: NotificationSender,
    val notificationSchedulerService: NotificationSchedulerService,
) {
    private val logger: KLogger = KotlinLogging.logger { }

    fun getJackpotBet(id: Long, executionType: BetExecutionType) =
        jackpotP2PBetRepository.findFirstByBetIdAndBaseBetExecutionType(id, executionType)

    @Transactional
    fun markJackpotFailed(id: Long, executionType: BetExecutionType, errorMessage: String?) =
        getJackpotBet(id, executionType)
            .apply {
                status = BetStatus.FAILED
                baseBet.errorMessage = errorMessage
            }
            .apply(jackpotP2PBetRepository::save)

    @Transactional
    fun onCreateJackpotBet(createLog: JackpotBetCreatedLog, executionType: BetExecutionType) =
        JackpotP2PBetEntity(
            betId = createLog.id.longValueExact(),
            requestAmount = BigDecimal(createLog.requestAmount) / (BigDecimal(10).pow(18)),
            startBank = BigDecimal(createLog.startBank) / (BigDecimal(10).pow(18)),
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
            .apply(jackpotP2PBetRepository::save)
            .also { logger.info { "Jackpot bet with id ${it.betId} created. Expiration time: ${it.baseBet.expirationDate}" } }
            .apply(jackpotBetSchedulerService::scheduleJackpotExpirationJob)
            .apply {
                notificationSchedulerService.scheduleLockTimeExpirationJob(
                    betId = betId,
                    betType = BetType.JACKPOT,
                    lockTime = baseBet.lockDate.minusMinutes(5)
                )

                notificationSchedulerService.scheduleLockTimeExpirationJob(
                    betId = betId,
                    betType = BetType.JACKPOT,
                    lockTime = baseBet.lockDate.minusMinutes(60)
                )
            }

    @Transactional
    fun onCloseJackpotBet(closeLog: JackpotBetClosedLog, executionType: BetExecutionType) =
        getJackpotBet(closeLog.betId.longValueExact(), executionType)
            .apply {
                status = BetStatus.CLOSED
                baseBet.closedTxHash = closeLog.transactionHash
                baseBet.closedBlockNumber = closeLog.blockNumber
                firstWonSize = closeLog.firstWonSize.longValueExact()
                secondWonSize = closeLog.secondWonSize.longValueExact()
                thirdWonSize = closeLog.thirdWonSize.longValueExact()
                totalRaffled = BigDecimal(closeLog.totalRaffled) / (BigDecimal(10).pow(18))
            }
            .apply(jackpotP2PBetRepository::save)
            .apply(jackpotBetSchedulerService::scheduleJackpotPostCloseJob)
            .also {
                jackpotJoinRepository.findAllByJackpotBetBetIdAndStatusIn(
                    it.betId, setOf(
                        JackpotJoinStatus.WON_FIRST,
                        JackpotJoinStatus.WON_SECOND,
                        JackpotJoinStatus.WON_THIRD,
                        JackpotJoinStatus.LOST,
                    )
                )
                    .forEach { join ->
                        notificationSender.send(
                            BetResultNotification(
                                betType = BetType.JACKPOT,
                                userId = join.client,
                                type = NotificationType.BET_RESULT,
                                betId = it.betId,
                                result = if (join.status == JackpotJoinStatus.LOST) BetResultNotification.Result.FAIL
                                else BetResultNotification.Result.WON
                            )
                        )
                    }

            }
            .also { logger.info { "Jackpot bet with id ${it.betId} closed." } }


    @Transactional
    fun creatingJackpotBet(
        eventId: UUID,
        lockPeriod: Long,
        expirationPeriod: Long,
        requestAmount: BigDecimal,
        executionType: BetExecutionType,
    ) {
        jackpotBetSchedulerService.scheduleJackpotCreateJob(
            eventId = eventId,
            lockPeriod = lockPeriod,
            expirationPeriod = expirationPeriod,
            requestAmount = requestAmount,
            executionType = executionType
        )
    }

    @Transactional
    fun onExpirationJackpot(jackpotP2PBetEntity: JackpotP2PBetEntity, finalValue: String) =
        jackpotP2PBetEntity
            .apply {
                status = BetStatus.PENDING_CLOSE
                this.finalValue = finalValue
            }
            .apply(jackpotP2PBetRepository::save)
            .apply(jackpotBetSchedulerService::scheduleJackpotCloseJob)

}
