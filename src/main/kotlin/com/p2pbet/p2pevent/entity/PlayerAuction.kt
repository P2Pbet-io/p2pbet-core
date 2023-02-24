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
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

//@Entity
//@Table(name = "player_auction")
//@EntityListeners(AuditingEntityListener::class)
class PlayerAuction(
    @Id
    var id: UUID = UUID.randomUUID(),
    val address: String,
    val auctionEventId: Long,
    val amount: BigDecimal,
    val targetValue: BigDecimal,
) {

    @ManyToOne
    @JoinColumn(name = "auction_event_id")
    lateinit var auctionEvent: AuctionEvent

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    lateinit var createdDate: LocalDateTime

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    lateinit var modifiedDate: LocalDateTime

}
