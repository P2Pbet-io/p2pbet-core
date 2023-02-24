package com.p2pbet.users.service.converter

import com.p2pbet.bet.auction.service.AuctionBetService
import com.p2pbet.bet.binary.service.BinaryBetService
import com.p2pbet.bet.custom.service.CustomBetService
import com.p2pbet.bet.jackpot.service.JackpotBetService
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.users.entity.ClientBetJoinEntity
import com.p2pbet.users.entity.ClientBetTransactionEntity
import com.p2pbet.users.rest.model.BetInfo
import com.p2pbet.users.rest.model.ClientJoinResponse
import com.p2pbet.users.rest.model.ClientTransactionResponse
import com.p2pbet.users.rest.model.CustomBetAdditionInfo
import org.springframework.stereotype.Service

@Service
class ClientConverterService(
    val auctionBetService: AuctionBetService,
    val customBetService: CustomBetService,
    val jackpotBetService: JackpotBetService,
    val binaryBetService: BinaryBetService,
) {
    fun convertToTransactionResponse(transaction: ClientBetTransactionEntity) = ClientTransactionResponse(
        betType = transaction.clientBetJoinEntity.betType,
        betId = transaction.clientBetJoinEntity.betId,
        logSc = transaction.logData,
        betInfo = transaction.clientBetJoinEntity.toBetInfo(),
        createdDate = transaction.createdDate
    )

    fun convertToJoinResponse(join: ClientBetJoinEntity) = with(join) {
        ClientJoinResponse(
            betType = betType,
            betId = betId,
            betInfo = toBetInfo(),
            totalJoinAmount = totalJoinAmount,
            joinStatus = joinStatus,
            expectedWonAmount = expectedWonAmount,
            amountTaken = amountTaken,
            createdDate = createdDate,
            modifiedDate = modifiedDate
        )
    }

    fun ClientBetJoinEntity.toBetInfo(): BetInfo = when (betType) {
        ContractType.CUSTOM -> with(customBetService.getCustomBet(betId, executionType)) {
            BetInfo(
                lockDate = this.baseBet.lockDate,
                expirationDate = this.baseBet.expirationDate,
                createdDate = this.createdDate,
                eventId = this.baseEvent.id,
                finalValue = this.finalValue,
                customBetAdditionInfo = CustomBetAdditionInfo(
                    targetValue = this.targetValue,
                    targetSide = this.targetSide
                )
            )
        }

        ContractType.BINARY -> with(binaryBetService.getBinaryBet(betId, executionType)) {
            BetInfo(
                lockDate = this.baseBet.lockDate,
                expirationDate = this.baseBet.expirationDate,
                createdDate = this.createdDate,
                eventId = this.baseEvent.id,
                finalValue = this.finalValue,
                customBetAdditionInfo = null
            )
        }

        ContractType.AUCTION -> with(auctionBetService.getAuctionBet(betId, executionType)) {
            BetInfo(
                lockDate = this.baseBet.lockDate,
                expirationDate = this.baseBet.expirationDate,
                createdDate = this.createdDate,
                eventId = this.baseEvent.id,
                finalValue = this.finalValue,
                customBetAdditionInfo = null
            )
        }

        ContractType.JACKPOT -> with(jackpotBetService.getJackpotBet(betId, executionType)) {
            BetInfo(
                lockDate = this.baseBet.lockDate,
                expirationDate = this.baseBet.expirationDate,
                createdDate = this.createdDate,
                eventId = this.baseEvent.id,
                finalValue = this.finalValue,
                customBetAdditionInfo = null
            )
        }
    }
}