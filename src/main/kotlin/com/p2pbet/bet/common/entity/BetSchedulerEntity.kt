package com.p2pbet.bet.common.entity

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.queue.ContractType
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "bet_schedule")
@EntityListeners(AuditingEntityListener::class)
class BetSchedulerEntity(
    @Id
    val id: UUID,
    @Column(name = "bet_type")
    @Enumerated(value = EnumType.STRING)
    var betType: ContractType,
    @Column(name = "bet_execution_type")
    @Enumerated(value = EnumType.STRING)
    var betExecutionType: BetExecutionType,
    @Column(name = "cron")
    var cron: String,
    @Column(name = "description")
    var description: String,
    @Column(name = "archive")
    var archive: Boolean,
    @Column(name = "request_amount")
    var requestAmount: BigDecimal?,
    @Column(name = "lock_period")
    val lockPeriod: Long,
    @Column(name = "expiration_period")
    val expirationPeriod: Long,
    @Column(name = "event_id")
    val eventId: UUID,
)