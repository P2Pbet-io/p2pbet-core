package com.p2pbet.users.repository

import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.users.entity.ClientBetJoinEntity
import com.p2pbet.users.repository.model.AggregatedClientStatusJoin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ClientBetJoinRepository : JpaRepository<ClientBetJoinEntity, UUID>,
    JpaSpecificationExecutor<ClientBetJoinEntity> {
    fun findAllByBetIdAndBetTypeAndClientAddress(
        betId: Long,
        betType: ContractType,
        clientAddress: String,
    ): List<ClientBetJoinEntity>

    @Query(
        value = """
            select 
                join_status as joinStatus,
                sum(total_join_amount) as totalJoin,
                coalesce(sum(expected_won_amount), 0) as expectedWonAmount,
                coalesce(sum(amount_taken), 0) as wonAmountTaken
                from client_bet_join where 
                client_address = ?1
            group by join_status
        """, nativeQuery = true
    )
    fun getAggregatedJoinByStatus(clientAddress: String): List<AggregatedClientStatusJoin>

}