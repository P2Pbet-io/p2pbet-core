package com.p2pbet.p2pevent.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table


//@Entity
//@Table(name = "auction_event")
//@EntityListeners(AuditingEntityListener::class)
class AuctionEvent(
    @Id
    @GeneratedValue
    var id: Long,
    val createdTxHash: String,
    val expirationTime: LocalDateTime,
    val lockTime: LocalDateTime,
    val execuionTime: LocalDateTime,
    var state: String,
    val finishedTime: LocalDateTime,
    val finishedTxHash: String,
    val finishedValue: BigDecimal,
    val winnerId: UUID,
) {

    @OneToOne
    @JoinColumn(name = "event_base_id")
    lateinit var baseEvent: BaseEvent

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    lateinit var createdDate: LocalDateTime

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    lateinit var modifiedDate: LocalDateTime

}
