package com.p2pbet.messaging.model.queue

import com.p2pbet.messaging.model.log.AbstractLog
import com.p2pbet.messaging.model.log.auction.*
import com.p2pbet.messaging.model.log.binary.*
import com.p2pbet.messaging.model.log.common.*
import com.p2pbet.messaging.model.log.custom.*
import com.p2pbet.messaging.model.log.jackpot.*
import kotlin.reflect.KClass

enum class LogEnumMapper(
    val extendedClass: KClass<out AbstractLog>,
) {
    //Common
    COMPANY_TRANSFERRED(TransferredCompanyLog::class),
    OWNER_TRANSFERRED(TransferredOwnershipLog::class),
    FEE_TAKEN(FeeTakenLog::class),
    COMPANY_FEE_CHANGED(CompanyFeeChangedLog::class),
    COMPANY_ALTER_FEE_CHANGED(CompanyAlterFeeChangedLog::class),

    //CUSTOM
    CUSTOM_CREATED(CustomBetCreatedLog::class),
    CUSTOM_JOINED(CustomBetJoinedLog::class),
    CUSTOM_CANCELED(CustomBetCanceledLog::class),
    CUSTOM_CLOSED(CustomBetClosedLog::class),
    CUSTOM_REFUNDED(CustomBetRefundedLog::class),
    CUSTOM_PRIZE_TAKEN(CustomBetPrizeTakenLog::class),

    //Binary
    BINARY_CREATED(BinaryBetCreatedLog::class),
    BINARY_JOINED(BinaryBetJoinedLog::class),
    BINARY_CANCELED(BinaryBetCanceledLog::class),
    BINARY_CLOSED(BinaryBetClosedLog::class),
    BINARY_REFUNDED(BinaryBetRefundedLog::class),
    BINARY_PRIZE_TAKEN(BinaryBetPrizeTakenLog::class),

    //Auction
    AUCTION_CREATED(AuctionBetCreatedLog::class),
    AUCTION_JOINED(AuctionBetJoinedLog::class),
    AUCTION_CANCELED(AuctionBetCanceledLog::class),
    AUCTION_CLOSED(AuctionBetClosedLog::class),
    AUCTION_REFUNDED(AuctionBetRefundedLog::class),
    AUCTION_PRIZE_TAKEN(AuctionBetPrizeTakenLog::class),

    //Jackpot
    JACKPOT_CREATED(JackpotBetCreatedLog::class),
    JACKPOT_JOINED(JackpotBetJoinedLog::class),
    JACKPOT_CANCELED(JackpotBetCanceledLog::class),
    JACKPOT_CLOSED(JackpotBetClosedLog::class),
    JACKPOT_REFUNDED(JackpotBetRefundedLog::class),
    JACKPOT_PRIZE_TAKEN(JackpotBetPrizeTakenLog::class);

    companion object {
        fun extractByLogName(logName: String) =
            LogEnumMapper.values()
                .first {
                    it.extendedClass.simpleName == logName
                }
    }
}