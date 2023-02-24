package com.p2pbet.bet.custom.entity

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
@Table(name = "custom_p2p_bet")
@EntityListeners(AuditingEntityListener::class)
class CustomP2PBetEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "bet_id")
    val betId: Long,
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    var status: BetStatus = BetStatus.CREATED,
    @Column(name = "target_value")
    val targetValue: String,
    @Column(name = "target_side")
    val targetSide: Boolean,
    @Column(name = "coefficient")
    val coefficient: BigDecimal,
    @Column(name = "creator")
    val creator: String,
    @Column(name = "hidden")
    val hidden: Boolean,
    @OneToOne
    @JoinColumn(name = "event_base_id")
    val baseEvent: BaseEvent,
) {
    @Column(name = "final_value")
    var finalValue: String? = null

    @Column(name = "target_side_won")
    var targetSideWon: Boolean? = null

    @OneToOne(mappedBy = "customBet")
    var matchingInfo: CustomMatchingEntity? = null

    @Embedded
    lateinit var baseBet: BaseBetEntity

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    lateinit var createdDate: LocalDateTime

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    lateinit var modifiedDate: LocalDateTime
}
