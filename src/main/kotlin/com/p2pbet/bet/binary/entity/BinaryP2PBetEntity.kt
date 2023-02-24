package com.p2pbet.bet.binary.entity

import com.p2pbet.bet.common.entity.BaseBetEntity
import com.p2pbet.bet.common.entity.enums.BetStatus
import com.p2pbet.p2pevent.entity.BaseEvent
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "binary_p2p_bet")
@EntityListeners(AuditingEntityListener::class)
class BinaryP2PBetEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "bet_id")
    val betId: Long,
    @Column(name = "left_amount")
    var leftAmount: BigDecimal = BigDecimal.ZERO,
    @Column(name = "right_amount")
    var rightAmount: BigDecimal = BigDecimal.ZERO,
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    var status: BetStatus = BetStatus.CREATED,
    @Column(name = "period")
    val period: Long,
    @OneToOne
    @JoinColumn(name = "event_base_id")
    val baseEvent: BaseEvent,
) {
    @Column(name = "locked_value")
    var lockedValue: String? = null

    @Column(name = "final_value")
    var finalValue: String? = null

    @Column(name = "side_won")
    var sideWon: Boolean? = null

    @Embedded
    lateinit var baseBet: BaseBetEntity

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    lateinit var createdDate: LocalDateTime

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    lateinit var modifiedDate: LocalDateTime

}
