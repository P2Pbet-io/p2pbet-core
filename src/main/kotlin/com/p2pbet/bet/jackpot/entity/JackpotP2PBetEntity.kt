package com.p2pbet.bet.jackpot.entity

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
@Table(name = "jackpot_p2p_bet")
@EntityListeners(AuditingEntityListener::class)
class JackpotP2PBetEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "bet_id")
    val betId: Long,
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    var status: BetStatus = BetStatus.CREATED,
    @Column(name = "request_amount")
    val requestAmount: BigDecimal,
    @Column(name = "start_bank")
    val startBank: BigDecimal,
    @OneToOne
    @JoinColumn(name = "event_base_id")
    val baseEvent: BaseEvent,
) {
    @Column(name = "final_value")
    var finalValue: String? = null

    @Column(name = "first_won_size")
    var firstWonSize: Long? = null

    @Column(name = "second_won_size")
    var secondWonSize: Long? = null

    @Column(name = "third_won_size")
    var thirdWonSize: Long? = null

    @Column(name = "total_raffled")
    var totalRaffled: BigDecimal? = null

    @Embedded
    lateinit var baseBet: BaseBetEntity

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    lateinit var createdDate: LocalDateTime

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    lateinit var modifiedDate: LocalDateTime

}