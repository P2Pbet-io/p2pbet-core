package com.p2pbet.bet.jackpot.entity.enums

enum class JackpotJoinStatus {
    JOINED,
    CANCELED,
    WON_FIRST,
    WON_SECOND,
    WON_THIRD,
    PRIZE_TAKEN_FIRST,
    PRIZE_TAKEN_SECOND,
    PRIZE_TAKEN_THIRD,
    LOST,
    REFUNDED;

    companion object {
        fun JackpotJoinStatus.isWon() = this in listOf(
            WON_FIRST,
            WON_SECOND,
            WON_THIRD,
            PRIZE_TAKEN_FIRST,
            PRIZE_TAKEN_SECOND,
            PRIZE_TAKEN_THIRD
        )
    }
}