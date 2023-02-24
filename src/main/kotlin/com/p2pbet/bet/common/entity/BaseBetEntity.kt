package com.p2pbet.bet.common.entity

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import java.math.BigInteger
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class BaseBetEntity() {
    @Column(name = "lock_date", nullable = false)
    lateinit var lockDate: LocalDateTime

    @Column(name = "execution_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    lateinit var executionType: BetExecutionType

    @Column(name = "expiration_date", nullable = false)
    lateinit var expirationDate: LocalDateTime

    @Column(name = "created_tx_hash")
    lateinit var createdTxHash: String

    @Column(name = "created_block_number")
    lateinit var createdBlockNumber: BigInteger

    @Column(name = "closed_tx_hash")
    var closedTxHash: String? = null

    @Column(name = "closed_block_number")
    var closedBlockNumber: BigInteger? = null

    @Column(name = "error_message")
    var errorMessage: String? = null
}