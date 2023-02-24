package com.p2pbet.bet.custom.service

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.custom.entity.CustomJoinEntity
import com.p2pbet.bet.custom.entity.CustomMatchingEntity
import com.p2pbet.bet.custom.entity.CustomP2PBetEntity
import com.p2pbet.bet.custom.repository.CustomMatchingRepository
import com.p2pbet.messaging.model.log.custom.CustomBetCanceledLog
import com.p2pbet.messaging.model.log.custom.CustomBetJoinedLog
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.messaging.notification.BetType
import com.p2pbet.messaging.notification.MoneyAcceptedNotification
import com.p2pbet.messaging.notification.Notification
import com.p2pbet.messaging.notification.NotificationType
import com.p2pbet.notification.NotificationSender
import com.p2pbet.users.service.ClientBetTransactionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO
import java.math.BigInteger

@Service
class CustomMatchingService(
    val customMatchingRepository: CustomMatchingRepository,
    val customJoinService: CustomJoinService,
    val customBetService: CustomBetService,
    val clientBetTransactionService: ClientBetTransactionService,
    val notificationSender: NotificationSender,
) {
    fun onCreate(customP2PBetEntity: CustomP2PBetEntity) =
        CustomMatchingEntity(
            customBet = customP2PBetEntity,
            baseBetId = customP2PBetEntity.betId
        ).apply(customMatchingRepository::save)

    @Transactional
    fun onCustomJoin(joinLog: CustomBetJoinedLog, executionType: BetExecutionType) =
        getCustomMatchingInfo(joinLog.betId, executionType)
            .let {
                matchJoin(
                    customMatching = it,
                    newJoin = customJoinService.formJoin(it.customBet, joinLog)
                )
            }
            .apply(customJoinService::updateOrSaveJoin)
            .apply {
                clientBetTransactionService.onJoin(
                    betId = this.customBet.betId,
                    type = ContractType.CUSTOM,
                    clientAddress = this.client,
                    joinAmount = this.joinAmount,
                    joinExternalId = this.id,
                    data = joinLog,
                    executionType = executionType
                )
            }

    @Transactional
    fun onCustomCancel(cancelLog: CustomBetCanceledLog, executionType: BetExecutionType) =
        getCustomMatchingInfo(cancelLog.betId, executionType)
            .apply {
                val join = customJoinService.findFirstByJoinIdClientRefAndClientAndCustomBetId(cancelLog, executionType)
                if (join.side) {
                    leftFreeAmount -= join.freeAmount
                } else {
                    rightFreeAmount -= join.freeAmount
                }
                clientBetTransactionService.onCancel(
                    betId = join.customBet.betId,
                    type = ContractType.CUSTOM,
                    clientAddress = join.client,
                    cancelAmount = join.freeAmount,
                    joinExternalId = join.id,
                    data = cancelLog,
                    executionType = executionType
                )

                customJoinService.onCustomCancel(cancelLog, join)
            }
            .apply {
                totalPool = leftFreeAmount + leftLockedAmount + rightFreeAmount + rightLockedAmount
                totalJoinCount = leftSize + rightSize
            }
            .apply(customMatchingRepository::save)


    private fun getCustomMatchingInfo(betId: BigInteger, executionType: BetExecutionType): CustomMatchingEntity =
        betId
            .let(BigInteger::longValueExact)
            .let { customBetService.getCustomBet(it, executionType) }
            .let(customMatchingRepository::findFirstByCustomBet)

    private fun matchJoin(customMatching: CustomMatchingEntity, newJoin: CustomJoinEntity): CustomJoinEntity {
        newJoin.freeAmount = newJoin.joinAmount
        newJoin.lockedAmount = ZERO

        if (newJoin.side) {
            customMatching.processLeft(newJoin)
        } else {
            customMatching.processRight(newJoin)
        }
            .apply {
                totalPool = leftFreeAmount + leftLockedAmount + rightFreeAmount + rightLockedAmount
                totalJoinCount = leftSize + rightSize
            }
            .apply(customMatchingRepository::save)

        return newJoin
    }

    private fun CustomMatchingEntity.processLeft(newJoin: CustomJoinEntity): CustomMatchingEntity {
        leftSize++;
        if (leftFreeAmount.compareTo(ZERO) == 0) {
            // Map to other side and update new join
            rightLastId = mapToOtherSide(
                getOtherJoin = {
                    customJoinService.getJoin(
                        side = false,
                        customBet = customBet,
                        joinId = it
                    )
                },
                otherLastId = rightLastId,
                otherSize = rightSize,
                newJoin = newJoin
            )
        }

        leftFreeAmount += newJoin.freeAmount
        leftLockedAmount += newJoin.lockedAmount

        rightFreeAmount -= applyPureCoefficient(newJoin.lockedAmount, true)
        rightLockedAmount += applyPureCoefficient(newJoin.lockedAmount, true)
        return this
    }

    private fun CustomMatchingEntity.processRight(newJoin: CustomJoinEntity): CustomMatchingEntity {
        rightSize++;
        if (rightFreeAmount.compareTo(ZERO) == 0) {
            // Map to other side and update new join
            leftLastId = mapToOtherSide(
                getOtherJoin = {
                    customJoinService.getJoin(
                        side = true,
                        customBet = customBet,
                        joinId = it
                    )
                },
                otherLastId = leftLastId,
                otherSize = leftSize,
                newJoin = newJoin
            )
        }

        rightFreeAmount += newJoin.freeAmount
        rightLockedAmount += newJoin.lockedAmount

        leftFreeAmount -= applyPureCoefficient(newJoin.lockedAmount, false)
        leftLockedAmount += applyPureCoefficient(newJoin.lockedAmount, false)
        return this
    }

    fun CustomMatchingEntity.mapToOtherSide(
        getOtherJoin: (Long) -> CustomJoinEntity,
        otherLastId: Long,
        otherSize: Long,
        newJoin: CustomJoinEntity,
    ): Long {
        // End of other side
        if (otherLastId == otherSize) {
            return otherLastId;
        }

        val otherJoin = getOtherJoin(otherLastId)

        // Found cancelled bet or full bet, skip it
        if (otherJoin.freeAmount.compareTo(ZERO) == 0) {
            return mapToOtherSide(getOtherJoin, otherLastId + 1, otherSize, newJoin)
        }

        val freeAmountWithCoefficient = applyPureCoefficient(newJoin.freeAmount, newJoin.side)

        // Other join full locked current
        if (otherJoin.freeAmount >= freeAmountWithCoefficient) {
            otherJoin.freeAmount -= freeAmountWithCoefficient
            otherJoin.lockedAmount += freeAmountWithCoefficient
            customJoinService.updateOrSaveJoin(otherJoin)
            otherJoin.sendMoneyAcceptedEvent()
            newJoin.lockedAmount += newJoin.freeAmount
            newJoin.freeAmount = ZERO
            return otherLastId
        }

        // Current join free for than other join free
        val otherFreeAmountWithCoefficient = applyPureCoefficient(otherJoin.freeAmount, !newJoin.side)

        newJoin.freeAmount -= otherFreeAmountWithCoefficient;
        newJoin.lockedAmount += otherFreeAmountWithCoefficient;

        otherJoin.lockedAmount += otherJoin.freeAmount
        otherJoin.freeAmount = ZERO
        otherJoin.sendMoneyAcceptedEvent()
        customJoinService.updateOrSaveJoin(otherJoin)
        return mapToOtherSide(getOtherJoin, otherLastId + 1, otherSize, newJoin)
    }


    private fun CustomMatchingEntity.applyPureCoefficient(amount: BigDecimal, direct: Boolean): BigDecimal {
        if (amount.compareTo(ZERO) == 0) {
            return ZERO
        }

        return applyCoefficient(amount, direct) - amount
    }

    private fun CustomMatchingEntity.applyCoefficient(amount: BigDecimal, direct: Boolean): BigDecimal {
        if (amount.compareTo(ZERO) == 0) {
            return ZERO
        }
        return if (direct) {
            amount * customBet.coefficient
        } else {
            amount * (customBet.coefficient / (customBet.coefficient - ONE));
        }
    }

    private fun CustomJoinEntity.sendMoneyAcceptedEvent() =
        notificationSender.send(
            MoneyAcceptedNotification(
                userId = client,
                type = NotificationType.MONEY_ACCEPTED,
                betId = customBet.betId,
                betType = BetType.CUSTOM,
            ),
        )

}
