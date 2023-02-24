package com.p2pbet.users.entity

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.users.entity.enums.ClientBetJoinStatus
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "client_bet_join")
@EntityListeners(AuditingEntityListener::class)
data class ClientBetJoinEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "bet_id")
    val betId: Long,
    @Column(name = "bet_type")
    @Enumerated(value = EnumType.STRING)
    val betType: ContractType,
    @Column(name = "execution_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    val executionType: BetExecutionType,
    @Column(name = "client_address")
    val clientAddress: String,
    @Column(name = "created_date", nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now(),
) {
    @Column(name = "total_join_amount")
    var totalJoinAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "join_status")
    @Enumerated(value = EnumType.STRING)
    var joinStatus: ClientBetJoinStatus = ClientBetJoinStatus.JOINED

    @Column(name = "expected_won_amount")
    var expectedWonAmount: BigDecimal? = null

    @Column(name = "amount_taken")
    var amountTaken: BigDecimal? = null

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    lateinit var modifiedDate: LocalDateTime
}