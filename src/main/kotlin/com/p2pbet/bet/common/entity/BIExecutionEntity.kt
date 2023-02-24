package com.p2pbet.bet.common.entity

import com.p2pbet.bet.common.entity.enums.ExecutionAction
import com.p2pbet.client.bi.api.model.execution.ExecutionStatus
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "bi_execution")
@EntityListeners(AuditingEntityListener::class)
class BIExecutionEntity(
    @Id
    val id: UUID,
    @Column(name = "bet_id")
    var betId: Long?,
    @Column(name = "execution_status")
    @Enumerated(value = EnumType.STRING)
    var executionStatus: ExecutionStatus,
    @Column(name = "execution_action")
    @Enumerated(value = EnumType.STRING)
    val executionAction: ExecutionAction
) {
    @Column(name = "transaction_hash")
    var transactionHash: String? = null

    @Column(name = "error_message")
    var errorMessage: String? = null

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    lateinit var createdDate: LocalDateTime

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    lateinit var modifiedDate: LocalDateTime
}