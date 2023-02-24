package com.p2pbet.bet.binary.repository

import com.p2pbet.bet.binary.entity.BinaryP2PBetEntity
import com.p2pbet.bet.binary.repository.model.AggregatedPoolByBinaryBet
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BinaryP2PBetRepository : JpaRepository<BinaryP2PBetEntity, UUID>,
    JpaSpecificationExecutor<BinaryP2PBetEntity> {
    fun findFirstByBetIdAndBaseBetExecutionType(betId: Long, executionType: BetExecutionType): BinaryP2PBetEntity

    @Query(
        value = """
        select binary_bet_id as binaryBetId, side, sum(join_amount) as pool, count(*) as count from binary_join
            where not status = 'CANCELED' and binary_bet_id in ?1 and execution_type = ?2
        group by binary_bet_id, side
        order by binaryBetId desc
    """, nativeQuery = true
    )
    fun getAggregatedPoolByBinaryBets(
        binaryBetIds: List<Long>,
        executionType: String,
    ): List<AggregatedPoolByBinaryBet>
}