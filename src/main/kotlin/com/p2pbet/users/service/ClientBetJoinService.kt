package com.p2pbet.users.service

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.messaging.notification.BetType
import com.p2pbet.messaging.notification.CreateJoinNotification
import com.p2pbet.notification.NotificationSender
import com.p2pbet.messaging.notification.NotificationType
import com.p2pbet.users.entity.ClientBetJoinEntity
import com.p2pbet.users.entity.enums.ClientBetJoinStatus
import com.p2pbet.users.repository.ClientBetJoinRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class ClientBetJoinService(
    val clientBetJoinRepository: ClientBetJoinRepository,
    val notificationSender: NotificationSender,
) {
    @Transactional
    fun getOrCreate(betId: Long, type: ContractType, clientAddress: String, executionType: BetExecutionType) =
        clientBetJoinRepository.findAllByBetIdAndBetTypeAndClientAddress(
            betId = betId,
            betType = type,
            clientAddress = clientAddress
        ).firstOrNull()
            ?: ClientBetJoinEntity(
                betId = betId,
                betType = type,
                clientAddress = clientAddress,
                executionType = executionType
            ).apply(clientBetJoinRepository::save)
                .also {
                    notificationSender.send(
                        CreateJoinNotification(
                            userId = clientAddress,
                            type = NotificationType.CREATE_JOIN,
                            betId = betId,
                            betType = BetType.valueOf(type.name)
                        )
                    )
                }

    @Transactional
    fun onClose(
        betId: Long,
        type: ContractType,
        clientAddress: String,
        wonAmount: BigDecimal,
        executionType: BetExecutionType,
    ) =
        getOrCreate(
            betId = betId,
            type = type,
            clientAddress = clientAddress,
            executionType = executionType
        ).apply {
            this.joinStatus = ClientBetJoinStatus.CLOSED
            this.expectedWonAmount = wonAmount
        }.apply(this::update)

    fun update(join: ClientBetJoinEntity): ClientBetJoinEntity = clientBetJoinRepository.save(join)
}
