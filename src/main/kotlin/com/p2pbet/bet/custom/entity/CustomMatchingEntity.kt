package com.p2pbet.bet.custom.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "custom_matching")
@EntityListeners(AuditingEntityListener::class)
class CustomMatchingEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    @OneToOne
    @JoinColumn(name = "custom_bet_id_ref")
    val customBet: CustomP2PBetEntity,
    @Column(name = "custom_bet_id")
    val baseBetId: Long,
    @Column(name = "left_last_id")
    var leftLastId: Long = 0L,
    @Column(name = "left_size")
    var leftSize: Long = 0L,
    @Column(name = "right_last_id")
    var rightLastId: Long = 0L,
    @Column(name = "right_size")
    var rightSize: Long = 0L,
    @Column(name = "total_join_count")
    var totalJoinCount: Long = 0L,
    @Column(name = "left_free_amount")
    var leftFreeAmount: BigDecimal = BigDecimal.ZERO,
    @Column(name = "left_locked_amount")
    var leftLockedAmount: BigDecimal = BigDecimal.ZERO,
    @Column(name = "right_free_amount")
    var rightFreeAmount: BigDecimal = BigDecimal.ZERO,
    @Column(name = "right_locked_amount")
    var rightLockedAmount: BigDecimal = BigDecimal.ZERO,
    @Column(name = "total_pool")
    var totalPool: BigDecimal = BigDecimal.ZERO,
) {
    @CreatedDate
    @Column(name = "created_date", nullable = false)
    lateinit var createdDate: LocalDateTime

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    lateinit var modifiedDate: LocalDateTime
}