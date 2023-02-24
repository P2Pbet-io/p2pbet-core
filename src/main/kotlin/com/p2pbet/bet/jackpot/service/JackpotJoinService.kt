package com.p2pbet.bet.jackpot.service

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.jackpot.entity.JackpotJoinEntity
import com.p2pbet.bet.jackpot.entity.JackpotP2PBetEntity
import com.p2pbet.bet.jackpot.entity.enums.JackpotJoinStatus
import com.p2pbet.bet.jackpot.repository.JackpotJoinRepository
import com.p2pbet.messaging.model.log.jackpot.JackpotBetCanceledLog
import com.p2pbet.messaging.model.log.jackpot.JackpotBetJoinedLog
import com.p2pbet.messaging.model.log.jackpot.JackpotBetPrizeTakenLog
import com.p2pbet.messaging.model.log.jackpot.JackpotBetRefundedLog
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
import java.time.LocalDateTime

@Service
class JackpotJoinService(
    val jackpotBetService: JackpotBetService,
    val jackpotJoinRepository: JackpotJoinRepository,
    val eventService: EventService,
    val clientBetTransactionService: ClientBetTransactionService,
    val notificationSender: NotificationSender,
    ) {
    private val logger: KLogger = KotlinLogging.logger { }


    @Transactional
    fun onExpirationJackpot(jackpotId: Long, executionType: BetExecutionType): Unit =
        with(jackpotBetService.getJackpotBet(jackpotId, executionType)) {
            val finalValue = eventService.getValueByDate(baseEvent.id, baseBet.expirationDate).toString()

            if (!jackpotJoinRepository.existsByJackpotBetBetIdAndExecutionTypeAndStatus(
                    betId = jackpotId,
                    status = JackpotJoinStatus.JOINED,
                    executionType = executionType
                )
            ) {
                logger.info { "Expire jackpot $betId. No joins found." }
                jackpotBetService.onExpirationJackpot(
                    jackpotP2PBetEntity = this,
                    finalValue = finalValue
                )
                return
            }


            val firstWonSize = updateToWon(finalValue, JackpotJoinStatus.WON_FIRST)
            val secondWonSize = updateToWon(finalValue, JackpotJoinStatus.WON_SECOND)
            val thirdWonSize = updateToWon(finalValue, JackpotJoinStatus.WON_THIRD)

            val lostSize = updateOtherToLost()

            logger.info { "Expire jackpot $betId with. Lost size: $lostSize, First size: ${firstWonSize - secondWonSize}, Second size: ${secondWonSize - thirdWonSize}, Third size: $thirdWonSize" }

            jackpotBetService.onExpirationJackpot(
                jackpotP2PBetEntity = this,
                finalValue = finalValue
            )
        }

    private fun JackpotP2PBetEntity.updateToWon(finalValue: String, newStatus: JackpotJoinStatus) =
        jackpotJoinRepository.updateJoinsToWon(
            resultStatus = newStatus.name,
            date = LocalDateTime.now(),
            betId = betId,
            scale = when (newStatus) {
                JackpotJoinStatus.WON_FIRST -> 0
                JackpotJoinStatus.WON_SECOND -> 1
                JackpotJoinStatus.WON_THIRD -> 2
                else -> throw RuntimeException("Wrong new join status")
            },
            finalValue = finalValue,
            statuses = listOf(
                JackpotJoinStatus.JOINED,
                JackpotJoinStatus.WON_FIRST,
                JackpotJoinStatus.WON_SECOND,
                JackpotJoinStatus.WON_THIRD
            ).map(JackpotJoinStatus::name),
            executionType = baseBet.executionType.name
        )

    private fun JackpotP2PBetEntity.updateOtherToLost() =
        jackpotJoinRepository.updateOtherJoinsToLost(
            date = LocalDateTime.now(),
            bet = this,
            status = JackpotJoinStatus.JOINED,
            executionType = baseBet.executionType
        )

    @Transactional
    fun onJackpotJoin(joinLog: JackpotBetJoinedLog, executionType: BetExecutionType) =
        with(jackpotBetService.getJackpotBet(joinLog.betId.longValueExact(), executionType)) {
            JackpotJoinEntity(
                client = joinLog.client,
                jackpotBet = this,
                baseBetId = this.betId,
                joinId = joinLog.joinId.longValueExact(),
                joinIdClientRef = joinLog.joinIdRef.longValueExact(),
                targetValue = BigDecimal(joinLog.targetValue).setScale(2, RoundingMode.HALF_DOWN).toString(),
                joinHash = joinLog.transactionHash,
                executionType = executionType
            )
                .apply(jackpotJoinRepository::save)
                .apply {
                    clientBetTransactionService.onJoin(
                        betId = this.jackpotBet.betId,
                        type = ContractType.JACKPOT,
                        clientAddress = this.client,
                        joinAmount = this.jackpotBet.requestAmount,
                        joinExternalId = this.id,
                        data = joinLog,
                        executionType = executionType
                    )
                }
                .also { logger.info { "Caught jackpot join ${it.joinId} for bet with id ${it.jackpotBet.betId}" } }
        }

    @Transactional
    fun onJackpotCancel(cancelLog: JackpotBetCanceledLog, executionType: BetExecutionType) =
        jackpotJoinRepository
            .findFirstByJoinIdClientRefAndClientAndJackpotBetBetIdAndExecutionType(
                joinIdClientRef = cancelLog.joinIdRef.longValueExact(),
                client = cancelLog.client,
                betId = cancelLog.betId.longValueExact(),
                executionType = executionType
            )
            .apply {
                status = JackpotJoinStatus.CANCELED
                cancelHash = cancelLog.transactionHash
            }
            .apply(jackpotJoinRepository::save)
            .apply {
                clientBetTransactionService.onCancel(
                    betId = this.jackpotBet.betId,
                    type = ContractType.JACKPOT,
                    clientAddress = this.client,
                    cancelAmount = this.jackpotBet.requestAmount,
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
                        betId = it.jackpotBet.betId,
                        betType = BetType.JACKPOT,
                    )
                )
            }
            .also { logger.info { "Caught jackpot cancel ${it.joinId} for bet with id ${it.jackpotBet.betId}" } }

    @Transactional
    fun onJackpotRefund(refundLog: JackpotBetRefundedLog, executionType: BetExecutionType) =
        jackpotJoinRepository
            .findAllByClientAndJackpotBetBetIdAndExecutionTypeAndStatus(
                betId = refundLog.betId.longValueExact(),
                client = refundLog.clientAddress,
                status = JackpotJoinStatus.JOINED,
                executionType = executionType
            )
            .onEach {
                it.status = JackpotJoinStatus.REFUNDED
                it.refundHash = refundLog.transactionHash
            }
            .apply(jackpotJoinRepository::saveAll)
            .also {
                clientBetTransactionService.onRefund(
                    betId = refundLog.betId.longValueExact(),
                    type = ContractType.JACKPOT,
                    clientAddress = refundLog.clientAddress,
                    data = refundLog,
                    executionType = executionType
                )
            }
            .also { logger.info { "Caught jackpot refund for client ${refundLog.clientAddress} and bet with id ${refundLog.betId}" } }

    @Transactional
    fun onJackpotPrizeTaken(prizeTakenLog: JackpotBetPrizeTakenLog, executionType: BetExecutionType) =
        jackpotJoinRepository
            .findAllByJackpotBetBetIdAndExecutionTypeAndClientAndStatusIn(
                betId = prizeTakenLog.betId.longValueExact(),
                client = prizeTakenLog.clientAddress,
                status = listOf(JackpotJoinStatus.WON_FIRST, JackpotJoinStatus.WON_SECOND, JackpotJoinStatus.WON_THIRD),
                executionType = executionType
            )
            .onEach {
                it.prizeTakenHash = prizeTakenLog.transactionHash
                it.status = when (it.status) {
                    JackpotJoinStatus.WON_FIRST -> JackpotJoinStatus.PRIZE_TAKEN_FIRST
                    JackpotJoinStatus.WON_SECOND -> JackpotJoinStatus.PRIZE_TAKEN_SECOND
                    JackpotJoinStatus.WON_THIRD -> JackpotJoinStatus.PRIZE_TAKEN_THIRD
                    else -> JackpotJoinStatus.LOST
                }
            }
            .apply(jackpotJoinRepository::saveAll)
            .also {
                clientBetTransactionService.onPrizeTaken(
                    betId = prizeTakenLog.betId.longValueExact(),
                    type = ContractType.JACKPOT,
                    clientAddress = prizeTakenLog.clientAddress,
                    data = prizeTakenLog,
                    amountTaken = prizeTakenLog.amount.toBigDecimal().setScale(18, RoundingMode.HALF_DOWN)
                        .divide(BigDecimal(10).pow(18).setScale(18, RoundingMode.HALF_DOWN)),
                    executionType = executionType
                )
            }
            .also { logger.info { "Caught jackpot prize taken for client ${prizeTakenLog.clientAddress} for bet with id ${prizeTakenLog.betId}" } }
}
