package com.p2pbet.bet.custom.entity

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "custom_join")
@EntityListeners(AuditingEntityListener::class)
class CustomJoinEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    var status: JoinStatus = JoinStatus.JOINED,
    @Column(name = "client")
    val client: String,
    @ManyToOne
    @JoinColumn(name = "custom_bet_id_ref")
    val customBet: CustomP2PBetEntity,
    @Column(name = "custom_bet_id")
    val baseBetId: Long,
    @Column(name = "join_id")
    val joinId: Long,
    @Column(name = "join_id_client_ref")
    val joinIdClientRef: Long,
    @Column(name = "side")
    val side: Boolean,
    @Column(name = "join_amount")
    val joinAmount: BigDecimal,
    @Column(name = "join_hash")
    val joinHash: String,
    @Column(name = "execution_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    val executionType: BetExecutionType,
) {
    @Column(name = "free_amount")
    lateinit var freeAmount: BigDecimal

    @Column(name = "locked_amount")
    lateinit var lockedAmount: BigDecimal

    @Column(name = "cancel_hash")
    var cancelHash: String? = null

    @Column(name = "refund_hash")
    var refundHash: String? = null

    @Column(name = "prize_taken_hash")
    var prizeTakenHash: String? = null

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    lateinit var createdDate: LocalDateTime

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    lateinit var modifiedDate: LocalDateTime
}