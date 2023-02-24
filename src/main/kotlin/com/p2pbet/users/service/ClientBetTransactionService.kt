package com.p2pbet.users.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.users.entity.ClientBetJoinEntity
import com.p2pbet.users.entity.ClientBetTransactionEntity
import com.p2pbet.users.entity.enums.ClientBetJoinStatus
import com.p2pbet.users.repository.ClientBetTransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Service
class ClientBetTransactionService(
    val clientBetTransactionRepository: ClientBetTransactionRepository,
    val clientBetJoinService: ClientBetJoinService,
    val mapper: ObjectMapper,
) {
    @Transactional
    fun <T : AbstractLog> onJoin(
        betId: Long,
        type: ContractType,
        clientAddress: String,
        joinAmount: BigDecimal,
        joinExternalId: UUID,
        executionType: BetExecutionType,
        data: T,
    ): Unit = clientBetJoinService
        .getOrCreate(
            betId = betId,
            type = type,
            clientAddress = clientAddress,
            executionType = executionType
        )
        .apply {
            this.totalJoinAmount += joinAmount
        }
        .apply(clientBetJoinService::update)
        .createTransaction(data, joinExternalId)

    @Transactional
    fun <T : AbstractLog> onCancel(
        betId: Long,
        type: ContractType,
        clientAddress: String,
        cancelAmount: BigDecimal,
        joinExternalId: UUID,
        executionType: BetExecutionType,
        data: T,
    ): Unit = clientBetJoinService
        .getOrCreate(
            betId = betId,
            type = type,
            clientAddress = clientAddress,
            executionType = executionType
        )
        .apply {
            this.totalJoinAmount -= cancelAmount
        }
        .apply(clientBetJoinService::update)
        .createTransaction(data, joinExternalId)


    @Transactional
    fun <T : AbstractLog> onRefund(
        betId: Long,
        type: ContractType,
        clientAddress: String,
        executionType: BetExecutionType,
        data: T,
    ): Unit = clientBetJoinService
        .getOrCreate(
            betId = betId,
            type = type,
            clientAddress = clientAddress,
            executionType = executionType
        )
        .apply {
            this.joinStatus = ClientBetJoinStatus.REFUND
        }
        .apply(clientBetJoinService::update)
        .createTransaction(data, null)


    @Transactional
    fun <T : AbstractLog> onPrizeTaken(
        betId: Long,
        type: ContractType,
        clientAddress: String,
        amountTaken: BigDecimal,
        executionType: BetExecutionType,
        data: T,
    ): Unit = clientBetJoinService
        .getOrCreate(
            betId = betId,
            type = type,
            clientAddress = clientAddress,
            executionType = executionType
        )
        .apply {
            this.joinStatus = ClientBetJoinStatus.PRIZE_TAKEN
            this.amountTaken = amountTaken
        }
        .apply(clientBetJoinService::update)
        .createTransaction(data, null)


    private fun <T : AbstractLog> ClientBetJoinEntity.createTransaction(
        data: T,
        externalJoinId: UUID?,
    ): Unit = ClientBetTransactionEntity(
        clientBetJoinEntity = this,
        logData = mapper.convertValue(data, Map::class.java),
        transactionType = data.logType,
        joinExternalId = externalJoinId
    ).apply(clientBetTransactionRepository::save).let { }
}