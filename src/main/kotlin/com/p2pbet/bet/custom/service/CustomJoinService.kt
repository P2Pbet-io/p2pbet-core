package com.p2pbet.bet.custom.service

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.bet.custom.entity.CustomJoinEntity
import com.p2pbet.bet.custom.entity.CustomP2PBetEntity
import com.p2pbet.bet.custom.repository.CustomJoinRepository
import com.p2pbet.messaging.model.log.custom.CustomBetCanceledLog
import com.p2pbet.messaging.model.log.custom.CustomBetJoinedLog
import com.p2pbet.messaging.model.log.custom.CustomBetPrizeTakenLog
import com.p2pbet.messaging.model.log.custom.CustomBetRefundedLog
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.messaging.notification.BetType
import com.p2pbet.messaging.notification.DeleteBetNotification
import com.p2pbet.notification.NotificationSender
import com.p2pbet.messaging.notification.NotificationType
import com.p2pbet.p2pevent.service.EventService
import com.p2pbet.users.service.ClientBetTransactionService
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class CustomJoinService(
    val customBetService: CustomBetService,
    val customJoinRepository: CustomJoinRepository,
    val eventService: EventService,
    val clientBetTransactionService: ClientBetTransactionService,
    val notificationSender: NotificationSender,
) {
    private val logger: KLogger = KotlinLogging.logger { }

    @Transactional
    fun onExpirationCustomBet(customId: Long, executionType: BetExecutionType): Unit =
        with(customBetService.getCustomBet(customId, executionType)) {
            this.apply {
                apply {
                    val targetDecimalValue = BigDecimal(targetValue).setScale(2, RoundingMode.HALF_DOWN)
                    val finalDecimalValue = eventService.getValueByDate(baseEvent.id, baseBet.expirationDate)
                    finalValue = finalDecimalValue.toString()
                    targetSideWon = targetDecimalValue > finalDecimalValue == targetSide
                }
            }

            if (!customJoinRepository.existsByCustomBetBetIdAndStatusAndExecutionType(
                    betId = customId,
                    status = JoinStatus.JOINED,
                    executionType = executionType
                )
            ) {
                customBetService.onExpirationCustomBetMissed(this)
                return
            }

            // Update won
            customJoinRepository.updateJoinsToStatus(
                newStatus = JoinStatus.WON,
                date = java.time.LocalDateTime.now(),
                bet = this,
                wonSide = targetSideWon!!,
                status = JoinStatus.JOINED,
                executionType = executionType
            )

        // Update lost
        customJoinRepository.updateJoinsToStatus(
            newStatus = JoinStatus.LOST,
            date = java.time.LocalDateTime.now(),
            bet = this,
            wonSide = !targetSideWon!!,
            status = JoinStatus.JOINED,
            executionType = executionType
        )

        customBetService.onExpirationCustomBetSuccess(
            customP2PBetEntity = this
        )
    }

    fun formJoin(customP2PBetEntity: CustomP2PBetEntity, joinLog: CustomBetJoinedLog) =
        CustomJoinEntity(
            client = joinLog.client,
            customBet = customP2PBetEntity,
            baseBetId = customP2PBetEntity.betId,
            joinId = joinLog.joinId.longValueExact(),
            joinIdClientRef = joinLog.joinIdRef.longValueExact(),
            side = joinLog.side,
            joinAmount = BigDecimal(joinLog.mainAmount).setScale(18, RoundingMode.HALF_DOWN) / BigDecimal(10).pow(18),
            joinHash = joinLog.transactionHash,
            executionType = customP2PBetEntity.baseBet.executionType
        )

    @Transactional
    fun onCustomCancel(cancelLog: CustomBetCanceledLog, customJoin: CustomJoinEntity) =
        customJoin
            .apply {
                freeAmount = BigDecimal.ZERO
                if (lockedAmount.compareTo(BigDecimal.ZERO) == 0) {
                    status = JoinStatus.CANCELED
                }
                cancelHash = cancelLog.transactionHash
            }
            .apply(customJoinRepository::save)
            .also {
                notificationSender.send(
                    DeleteBetNotification(
                        userId = it.client,
                        type = NotificationType.DELETE_BET,
                        betId = it.customBet.betId,
                        betType = BetType.CUSTOM,
                ))
            }
            .also { logger.info { "Caught custom cancel ${it.joinId} for bet with id ${it.customBet.betId}" } }

    @Transactional
    fun onCustomRefund(refundLog: CustomBetRefundedLog, executionType: BetExecutionType) =
        customJoinRepository
            .findAllByClientAndCustomBetBetIdAndStatusAndExecutionType(
                betId = refundLog.betId.longValueExact(),
                client = refundLog.clientAddress,
                status = JoinStatus.JOINED,
                executionType = executionType
            )
            .onEach {
                it.status = JoinStatus.REFUNDED
                it.refundHash = refundLog.transactionHash
            }
            .apply(customJoinRepository::saveAll)
            .also {
                clientBetTransactionService.onRefund(
                    betId = refundLog.betId.longValueExact(),
                    type = ContractType.CUSTOM,
                    clientAddress = refundLog.clientAddress,
                    data = refundLog,
                    executionType = executionType
                )
            }
            .also { logger.info { "Caught custom refund for client ${refundLog.clientAddress} and bet with id ${refundLog.betId}" } }

    @Transactional
    fun onCustomPrizeTaken(prizeTakenLog: CustomBetPrizeTakenLog, executionType: BetExecutionType) =
        customJoinRepository
            .findAllByClientAndCustomBetBetIdAndStatusAndExecutionType(
                betId = prizeTakenLog.betId.longValueExact(),
                client = prizeTakenLog.clientAddress,
                status = JoinStatus.WON,
                executionType = executionType
            )
            .onEach {
                it.prizeTakenHash = prizeTakenLog.transactionHash
                it.status = JoinStatus.PRIZE_TAKEN
            }
            .apply(customJoinRepository::saveAll)
            .also {
                clientBetTransactionService.onPrizeTaken(
                    betId = prizeTakenLog.betId.longValueExact(),
                    type = ContractType.CUSTOM,
                    clientAddress = prizeTakenLog.clientAddress,
                    data = prizeTakenLog,
                    amountTaken = prizeTakenLog.amount.toBigDecimal().setScale(18, RoundingMode.HALF_DOWN)
                        .divide(BigDecimal(10).pow(18).setScale(18, RoundingMode.HALF_DOWN)),
                    executionType = executionType
                )
            }
            .also { logger.info { "Caught custom prize taken for client ${prizeTakenLog.clientAddress} for bet with id ${prizeTakenLog.betId}" } }


    @Transactional
    fun updateOrSaveJoin(customJoinEntity: CustomJoinEntity) = customJoinEntity
        .apply(customJoinRepository::save)

    fun findFirstByJoinIdClientRefAndClientAndCustomBetId(
        cancelLog: CustomBetCanceledLog,
        executionType: BetExecutionType,
    ) = customJoinRepository
        .findFirstByJoinIdClientRefAndClientAndCustomBetBetIdAndExecutionType(
            joinIdClientRef = cancelLog.joinIdRef.longValueExact(),
            client = cancelLog.client,
            betId = cancelLog.betId.longValueExact(),
            executionType = executionType
        )

    @Transactional
    fun getJoin(
        side: Boolean,
        customBet: CustomP2PBetEntity,
        joinId: Long,
    ) = customJoinRepository.findFirstBySideAndCustomBetAndJoinId(
        side = side,
        customBet = customBet,
        joinId = joinId
    )

}
