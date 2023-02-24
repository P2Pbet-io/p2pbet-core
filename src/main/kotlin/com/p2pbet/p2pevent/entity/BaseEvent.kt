package com.p2pbet.p2pevent.entity

import com.p2pbet.p2pevent.controller.model.EventType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "base_event")
@EntityListeners(AuditingEntityListener::class)
class BaseEvent(
    @Id
    var id: UUID = UUID.randomUUID(),
    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    val type: EventType,
    @Column(name = "symbol")
    val symbol: String,
    @Column(name = "full_name")
    val fullName: String,
    @Column(name = "name")
    val name: String,
    @Column(name = "src_url")
    val srcUrl: String,
) {

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    lateinit var createdDate: LocalDateTime

}

