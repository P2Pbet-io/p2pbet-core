package com.p2pbet.bet.common.rest.model

import com.p2pbet.bet.common.repository.model.AggregatedUnionJoin
import com.p2pbet.messaging.model.queue.ContractType
import java.time.LocalDateTime
import java.util.*

data class UnionJoinResponse(
    val id: UUID,
    val betId: Long,
    val status: String,
    val betType: ContractType,
    val client: String,
    val joinHash: String,
    val cancelHash: String?,
    val refundHash: String?,
    val prizeTakenHash: String?,
    val createdDate: LocalDateTime,
    val modifiedDate: LocalDateTime,
) {
    companion object {
        fun AggregatedUnionJoin.toResponse() = UnionJoinResponse(
            id = UUID.fromString(id),
            betId = betId,
            status = status,
            betType = ContractType.valueOf(betType),
            client = client,
            joinHash = joinHash,
            cancelHash = cancelHash,
            refundHash = refundHash,
            prizeTakenHash = prizeTakenHash,
            createdDate = createdDate,
            modifiedDate = modifiedDate
        )
    }
}
