package com.p2pbet.bet.binary.service

import com.p2pbet.bet.binary.entity.BinaryJoinEntity
import com.p2pbet.bet.binary.repository.BinaryJoinRepository
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.messaging.model.log.binary.BinaryBetCanceledLog
import com.p2pbet.messaging.model.log.binary.BinaryBetJoinedLog
import com.p2pbet.messaging.model.log.binary.BinaryBetPrizeTakenLog
import com.p2pbet.messaging.model.log.binary.BinaryBetRefundedLog
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
class BinaryJoinService(
    val binaryJoinRepository: BinaryJoinRepository,
    val eventService: EventService,
    val binaryBetService: BinaryBetService,
    val clientBetTransactionService: ClientBetTransactionService,
    val notificationSender: NotificationSender,
    ) {
    private val logger: KLogger = KotlinLogging.logger { }

    @Transactional
    fun onExpirationBinary(binaryId: Long, executionType: BetExecutionType): Unit =
        with(binaryBetService.getBinaryBet(binaryId, executionType)) {
            this.apply {
                val lockDecimalValue = eventService.getValueByDate(baseEvent.id, baseBet.lockDate)
                val finalDecimalValue = eventService.getValueByDate(baseEvent.id, baseBet.expirationDate)
                lockedValue = lockDecimalValue.toString()
                finalValue = finalDecimalValue.toString()
                sideWon = finalDecimalValue > lockDecimalValue
            }

            if (!binaryJoinRepository.existsByBinaryBetBetIdAndStatusAndExecutionType(
                    betId = binaryId,
                    status = JoinStatus.JOINED,
                    executionType = executionType
                )
            ) {
                binaryBetService.onExpirationBinaryBetMissed(this)
                return
            }

            // Update won
            binaryJoinRepository.updateJoinsToStatus(
                newStatus = JoinStatus.WON,
                date = java.time.LocalDateTime.now(),
                bet = this,
                wonSide = sideWon!!,
                status = JoinStatus.JOINED,
                executionType = executionType
            )

            // Update lost
            binaryJoinRepository.updateJoinsToStatus(
                newStatus = JoinStatus.LOST,
                date = java.time.LocalDateTime.now(),
                bet = this,
                wonSide = !sideWon!!,
                status = JoinStatus.JOINED,
                executionType = executionType
            )

            binaryBetService.onExpirationBinaryBetSuccess(
                binaryP2PBetEntity = this
            )
        }

    @Transactional
    fun onBinaryJoin(joinLog: BinaryBetJoinedLog, executionType: BetExecutionType) =
        with(binaryBetService.getBinaryBet(joinLog.betId.longValueExact(), executionType)) {
            BinaryJoinEntity(
                client = joinLog.client,
                binaryBet = this,
                baseBetId = this.betId,
                joinId = joinLog.joinId.longValueExact(),
                joinIdClientRef = joinLog.joinIdRef.longValueExact(),
                joinHash = joinLog.transactionHash,
                side = joinLog.side,
                joinAmount = BigDecimal(joinLog.mainAmount).setScale(
                    18,
                    RoundingMode.HALF_DOWN
                ) / BigDecimal(10).pow(18),
                executionType = executionType
            )
                .apply(binaryJoinRepository::save)
                .apply {
                    clientBetTransactionService.onJoin(
                        betId = this.binaryBet.betId,
                        type = ContractType.BINARY,
                        clientAddress = this.client,
                        joinAmount = this.joinAmount,
                        joinExternalId = this.id,
                        data = joinLog,
                        executionType = executionType
                    )
                }
                .also { logger.info { "Caught binary join ${it.joinId} for bet with id ${it.binaryBet.betId}" } }
        }

    @Transactional
    fun onBinaryCancel(cancelLog: BinaryBetCanceledLog, executionType: BetExecutionType) =
        binaryJoinRepository
            .findFirstByJoinIdClientRefAndClientAndBinaryBetBetIdAndExecutionType(
                joinIdClientRef = cancelLog.joinIdRef.longValueExact(),
                client = cancelLog.client,
                betId = cancelLog.betId.longValueExact(),
                executionType = executionType
            )
            .apply {
                status = JoinStatus.CANCELED
                cancelHash = cancelLog.transactionHash
            }
            .apply(binaryJoinRepository::save)
            .apply {
                clientBetTransactionService.onCancel(
                    betId = this.binaryBet.betId,
                    type = ContractType.BINARY,
                    clientAddress = this.client,
                    cancelAmount = this.joinAmount,
                    joinExternalId = this.id,
                    data = cancelLog,
                    executionType = executionType
                )
            }
            .also {
                notificationSender.send(
                    DeleteBetNotification(
                        userId = it.client,
                        type = NotificationType.DELETE_BET,
                        betId = it.binaryBet.betId,
                        betType = BetType.BINARY,
                    )
                )
            }
            .also { logger.info { "Caught binary cancel ${it.joinId} for bet with id ${it.binaryBet.betId}" } }

    @Transactional
    fun onBinaryRefund(refundLog: BinaryBetRefundedLog, executionType: BetExecutionType) =
        binaryJoinRepository
            .findAllByClientAndBinaryBetBetIdAndStatusAndExecutionType(
                betId = refundLog.betId.longValueExact(),
                client = refundLog.clientAddress,
                status = JoinStatus.JOINED,
                executionType = executionType
            )
            .onEach {
                it.status = JoinStatus.REFUNDED
                it.refundHash = refundLog.transactionHash
            }
            .apply(binaryJoinRepository::saveAll)
            .also {
                clientBetTransactionService.onRefund(
                    betId = refundLog.betId.longValueExact(),
                    type = ContractType.BINARY,
                    clientAddress = refundLog.clientAddress,
                    data = refundLog,
                    executionType = executionType
                )
            }
            .also { logger.info { "Caught binary refund for client ${refundLog.clientAddress} and bet with id ${refundLog.betId}" } }

    @Transactional
    fun onBinaryPrizeTaken(prizeTakenLog: BinaryBetPrizeTakenLog, executionType: BetExecutionType) =
        binaryJoinRepository
            .findAllByClientAndBinaryBetBetIdAndStatusAndExecutionType(
                betId = prizeTakenLog.betId.longValueExact(),
                client = prizeTakenLog.clientAddress,
                status = JoinStatus.WON,
                executionType = executionType
            )
            .onEach {
                it.prizeTakenHash = prizeTakenLog.transactionHash
                it.status = JoinStatus.PRIZE_TAKEN
            }
            .apply(binaryJoinRepository::saveAll)
            .also {
                clientBetTransactionService.onPrizeTaken(
                    betId = prizeTakenLog.betId.longValueExact(),
                    type = ContractType.BINARY,
                    clientAddress = prizeTakenLog.clientAddress,
                    data = prizeTakenLog,
                    amountTaken = prizeTakenLog.amount.toBigDecimal().setScale(18, RoundingMode.HALF_DOWN)
                        .divide(BigDecimal(10).pow(18).setScale(18, RoundingMode.HALF_DOWN)),
                    executionType = executionType
                )
            }
            .also { logger.info { "Caught binary prize taken for client ${prizeTakenLog.clientAddress} for bet with id ${prizeTakenLog.betId}" } }

}
