package com.p2pbet.bet.auction.service

import com.p2pbet.bet.auction.entity.AuctionP2PBetEntity
import com.p2pbet.bet.auction.repository.AuctionJoinRepository
import com.p2pbet.bet.auction.repository.AuctionP2PBetRepository
import com.p2pbet.bet.common.entity.BaseBetEntity
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.BetStatus
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.messaging.model.log.auction.AuctionBetClosedLog
import com.p2pbet.messaging.model.log.auction.AuctionBetCreatedLog
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
class AuctionBetService(
    val auctionP2PBetRepository: AuctionP2PBetRepository,
    val auctionBetSchedulerService: AuctionBetSchedulerService,
    val eventService: EventService,
    val notificationSender: NotificationSender,
    val auctionJoinRepository: AuctionJoinRepository,
    val notificationSchedulerService: NotificationSchedulerService,
) {
    private val logger: KLogger = KotlinLogging.logger { }

    fun getAuctionBet(id: Long, executionType: BetExecutionType) =
        auctionP2PBetRepository.findFirstByBetIdAndBaseBetExecutionType(
            betId = id,
            executionType = executionType
        )

    @Transactional
    fun onExpirationAuctionMissed(auctionP2PBetEntity: AuctionP2PBetEntity, finalValue: String) = auctionP2PBetEntity
        .apply {
            this.status = BetStatus.SKIPPED_CLOSING
            this.finalValue = finalValue
        }
        .apply(auctionP2PBetRepository::save)
        .also { logger.info { "Auction bet with id ${it.betId} skipped. No joins found" } }

    @Transactional
    fun onExpirationAuctionSuccess(auctionP2PBetEntity: AuctionP2PBetEntity, finalValue: String) =
        auctionP2PBetEntity
            .apply {
                status = BetStatus.PENDING_CLOSE
                this.finalValue = finalValue
            }
            .apply(auctionP2PBetRepository::save)
            .apply(auctionBetSchedulerService::scheduleAuctionCloseJob)

    @Transactional
    fun onCloseAuctionBet(closeLog: AuctionBetClosedLog, executionType: BetExecutionType) =
        getAuctionBet(closeLog.betId.longValueExact(), executionType)
            .apply {
                status = BetStatus.CLOSED
                baseBet.closedTxHash = closeLog.transactionHash
                baseBet.closedBlockNumber = closeLog.blockNumber
            }
            .apply(auctionP2PBetRepository::save)
            .apply(auctionBetSchedulerService::scheduleAuctionPostCloseJob)
            .also {
                auctionJoinRepository.findAllByAuctionBetBetIdAndStatusIn(it.betId, setOf(JoinStatus.LOST, JoinStatus.WON))
                    .forEach {
                            join ->
                        notificationSender.send(
                            BetResultNotification(
                                betType = BetType.AUCTION,
                                userId = join.client,
                                type = NotificationType.BET_RESULT,
                                betId = it.betId,
                                result = if (join.status == JoinStatus.LOST) BetResultNotification.Result.FAIL
                                else BetResultNotification.Result.WON
                            )
                        )
                    }

            }
            .also { logger.info { "Auction bet with id ${it.betId} closed. Join ids won: ${closeLog.joinIdsWon}" } }


    @Transactional
    fun creatingAuctionBet(
        eventId: UUID,
        lockPeriod: Long,
        expirationPeriod: Long,
        requestAmount: BigDecimal,
        executionType: BetExecutionType,
    ) {
        auctionBetSchedulerService.scheduleAuctionCreateJob(
            eventId = eventId,
            lockPeriod = lockPeriod,
            expirationPeriod = expirationPeriod,
            requestAmount = requestAmount,
            executionType = executionType
        )
    }

    @Transactional
    fun onCreateAuctionBet(createLog: AuctionBetCreatedLog, executionType: BetExecutionType) =
        AuctionP2PBetEntity(
            betId = createLog.id.longValueExact(),
            requestAmount = BigDecimal(createLog.requestAmount) / (BigDecimal(10).pow(18)),
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
            .apply(auctionP2PBetRepository::save)
            .also { logger.info { "Auction bet with id ${it.betId} created. Expiration time: ${it.baseBet.expirationDate}" } }
            .apply(auctionBetSchedulerService::scheduleAuctionExpirationJob)
            .apply {
                notificationSchedulerService.scheduleLockTimeExpirationJob(
                    betId = betId,
                    betType = BetType.AUCTION,
                    lockTime = baseBet.lockDate.minusMinutes(5)
                )
            }

    @Transactional
    fun markAuctionFailed(id: Long, executionType: BetExecutionType, errorMessage: String?) =
        getAuctionBet(id, executionType)
            .apply {
                status = BetStatus.FAILED
                baseBet.errorMessage = errorMessage
            }
            .apply(auctionP2PBetRepository::save)
}
