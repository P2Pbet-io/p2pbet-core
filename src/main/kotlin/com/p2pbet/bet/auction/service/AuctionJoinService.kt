package com.p2pbet.bet.auction.service

import com.p2pbet.bet.auction.entity.AuctionJoinEntity
import com.p2pbet.bet.auction.repository.AuctionJoinRepository
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.messaging.model.log.auction.AuctionBetCanceledLog
import com.p2pbet.messaging.model.log.auction.AuctionBetJoinedLog
import com.p2pbet.messaging.model.log.auction.AuctionBetPrizeTakenLog
import com.p2pbet.messaging.model.log.auction.AuctionBetRefundedLog
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
class AuctionJoinService(
    val auctionBetService: AuctionBetService,
    val auctionJoinRepository: AuctionJoinRepository,
    val eventService: EventService,
    val clientBetTransactionService: ClientBetTransactionService,
    val notificationSender: NotificationSender,
    ) {
    private val logger: KLogger = KotlinLogging.logger { }

    @Transactional
    fun onExpirationAuction(auctionId: Long, executionType: BetExecutionType): Unit =
        with(auctionBetService.getAuctionBet(auctionId, executionType)) {
            val finalValue = eventService.getValueByDate(baseEvent.id, baseBet.expirationDate).toString()

            if (!auctionJoinRepository.existsByAuctionBetBetIdAndStatusAndExecutionType(
                    betId = auctionId,
                    status = JoinStatus.JOINED,
                    executionType = executionType
                )
            ) {
                auctionBetService.onExpirationAuctionMissed(
                    auctionP2PBetEntity = this,
                    finalValue = finalValue
                )
                return
            }


            val wonJoins = auctionJoinRepository.findWonJoin(
                finalValue = finalValue,
                joinStatus = JoinStatus.JOINED.name,
                betId = betId,
                executionType = executionType.name
            ).map {
                it.status = JoinStatus.WON
                it
            }.apply(auctionJoinRepository::saveAll)


            auctionJoinRepository.updateOtherJoinsToLost(
                date = LocalDateTime.now(),
                bet = this,
                wonIds = wonJoins.map { it.id },
                status = JoinStatus.JOINED,
                executionType = executionType
            )

            auctionBetService.onExpirationAuctionSuccess(
                auctionP2PBetEntity = this,
                finalValue = finalValue
            )
        }

    @Transactional
    fun onAuctionJoin(joinLog: AuctionBetJoinedLog, executionType: BetExecutionType) =
        with(auctionBetService.getAuctionBet(joinLog.betId.longValueExact(), executionType)) {
            AuctionJoinEntity(
                client = joinLog.client,
                auctionBet = this,
                baseBetId = this.betId,
                joinId = joinLog.joinId.longValueExact(),
                joinIdClientRef = joinLog.joinIdRef.longValueExact(),
                targetValue = BigDecimal(joinLog.targetValue).setScale(2, RoundingMode.HALF_DOWN).toString(),
                joinHash = joinLog.transactionHash,
                executionType = executionType
            )
                .apply(auctionJoinRepository::save)
                .apply {
                    clientBetTransactionService.onJoin(
                        betId = this.auctionBet.betId,
                        type = ContractType.AUCTION,
                        clientAddress = this.client,
                        joinAmount = this.auctionBet.requestAmount,
                        joinExternalId = this.id,
                        data = joinLog,
                        executionType = baseBet.executionType
                    )
                }
                .also { logger.info { "Caught auction join ${it.joinId} for bet with id ${it.auctionBet.betId}" } }
        }

    @Transactional
    fun onAuctionCancel(cancelLog: AuctionBetCanceledLog, executionType: BetExecutionType) =
        auctionJoinRepository
            .findFirstByJoinIdClientRefAndClientAndAuctionBetBetIdAndExecutionType(
                joinIdClientRef = cancelLog.joinIdRef.longValueExact(),
                client = cancelLog.client,
                betId = cancelLog.betId.longValueExact(),
                executionType = executionType
            )
            .apply {
                status = JoinStatus.CANCELED
                cancelHash = cancelLog.transactionHash
            }
            .apply(auctionJoinRepository::save)
            .apply {
                clientBetTransactionService.onCancel(
                    betId = this.auctionBet.betId,
                    type = ContractType.AUCTION,
                    clientAddress = this.client,
                    cancelAmount = this.auctionBet.requestAmount,
                    joinExternalId = this.id,
                    data = cancelLog,
                    executionType = auctionBet.baseBet.executionType
                )
            }
            .also {
                notificationSender.send(
                    DeleteBetNotification(
                        userId = it.client,
                        type = NotificationType.DELETE_BET,
                        betId = it.auctionBet.betId,
                        betType = BetType.AUCTION,
                    )
                )
            }
            .also { logger.info { "Caught auction cancel ${it.joinId} for bet with id ${it.auctionBet.betId}" } }

    @Transactional
    fun onAuctionRefund(refundLog: AuctionBetRefundedLog, executionType: BetExecutionType) =
        auctionJoinRepository
            .findAllByClientAndAuctionBetBetIdAndStatusAndExecutionType(
                betId = refundLog.betId.longValueExact(),
                client = refundLog.clientAddress,
                status = JoinStatus.JOINED,
                executionType = executionType
            )
            .onEach {
                it.status = JoinStatus.REFUNDED
                it.refundHash = refundLog.transactionHash
            }
            .apply(auctionJoinRepository::saveAll)
            .also {
                clientBetTransactionService.onRefund(
                    betId = refundLog.betId.longValueExact(),
                    type = ContractType.AUCTION,
                    clientAddress = refundLog.clientAddress,
                    data = refundLog,
                    executionType = executionType
                )
            }
            .also { logger.info { "Caught auction refund for client ${refundLog.clientAddress} and bet with id ${refundLog.betId}" } }

    @Transactional
    fun onAuctionPrizeTaken(prizeTakenLog: AuctionBetPrizeTakenLog, executionType: BetExecutionType) =
        auctionJoinRepository
            .findFirstByAuctionBetBetIdAndClientAndStatusAndExecutionType(
                betId = prizeTakenLog.betId.longValueExact(),
                client = prizeTakenLog.clientAddress,
                status = JoinStatus.WON,
                executionType = executionType
            )
            .apply {
                prizeTakenHash = prizeTakenLog.transactionHash
                status = JoinStatus.PRIZE_TAKEN
            }
            .apply(auctionJoinRepository::save)
            .also {
                clientBetTransactionService.onPrizeTaken(
                    betId = prizeTakenLog.betId.longValueExact(),
                    type = ContractType.AUCTION,
                    clientAddress = prizeTakenLog.clientAddress,
                    data = prizeTakenLog,
                    amountTaken = prizeTakenLog.amount.toBigDecimal().setScale(18, RoundingMode.HALF_DOWN)
                        .divide(BigDecimal(10).pow(18).setScale(18, RoundingMode.HALF_DOWN)),
                    executionType = executionType
                )
            }
            .also { logger.info { "Caught auction prize taken for client ${it.client} for bet with id ${it.auctionBet.betId}" } }

}
