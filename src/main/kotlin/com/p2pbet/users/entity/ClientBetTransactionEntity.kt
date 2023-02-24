package com.p2pbet.users.entity

import com.p2pbet.messaging.model.queue.LogEnumMapper
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "client_bet_transaction")
@TypeDef(name = "json", typeClass = JsonBinaryType::class)
data class ClientBetTransactionEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "join_external_id")
    val joinExternalId: UUID? = null,
    @ManyToOne
    @JoinColumn(name = "client_bet_join_id")
    val clientBetJoinEntity: ClientBetJoinEntity,
    @Column(name = "transaction_type")
    @Enumerated(value = EnumType.STRING)
    val transactionType: LogEnumMapper,
    @Type(type = "json")
    @Column(name = "log_data")
    val logData: Map<*, *>,
    @Column(name = "created_date", nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now(),
)
