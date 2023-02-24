package com.p2pbet.bet.common.repository

import com.p2pbet.bet.common.entity.BIExecutionEntity
import com.p2pbet.bet.common.repository.model.AggregatedUnionJoin
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UnionRepository : JpaRepository<BIExecutionEntity, UUID> {
    @Query(
        value = query, countQuery = "select count(*) from ($query) as aggregate_query", nativeQuery = true
    )
    fun getAggregatedUnionJoin(pageable: Pageable): Page<AggregatedUnionJoin>

    companion object {
        const val query: String = """
            with union_joins as (
                    select cast(id as varchar) as id, 
                        custom_bet_id as betId,
                        status as status,
                        join_amount as amount,
                        'CUSTOM' as betType,
                        client,
                        join_hash as joinHash, 
                        cancel_hash as cancelHash, 
                        refund_hash as refundHash,
                        prize_taken_hash as prizeTakenHash, 
                        created_date as createdDate,
                        modified_date as modifiedDate
                    from custom_join 
                union ALL
                    select cast(id as varchar) as id, 
                        binary_bet_id as betId,
                        status as status,
                        join_amount as amount,
                        'BINARY' as betType,
                        client,
                        join_hash as joinHash, 
                        cancel_hash as cancelHash, 
                        refund_hash as refundHash,
                        prize_taken_hash as prizeTakenHash, 
                        created_date as createdDate,
                        modified_date as modifiedDate
                    from binary_join bj 
                union ALL
                    select cast(aj.id as varchar) as id, 
                        aj.auction_bet_id as betId, 
                        aj.status as status,
                        ab.request_amount as amount,
                        'AUCTION' as betType,
                        aj.client,
                        aj.join_hash as joinHash, 
                        aj.cancel_hash as cancelHash, 
                        aj.refund_hash as refundHash,
                        aj.prize_taken_hash as prizeTakenHash, 
                        aj.created_date as createdDate,
                        aj.modified_date as modifiedDate
                    from auction_join aj 
                    left join auction_p2p_bet ab on aj.auction_bet_id_ref = ab.id 
                union ALL
                    select cast(jj.id as varchar) as id, 
                        jj.jackpot_bet_id as betId,
                        jj.status as status,
                        jb.request_amount as amount,
                        'JACKPOT' as betType,
                        jj.client,
                        jj.join_hash as joinHash, 
                        jj.cancel_hash as cancelHash, 
                        jj.refund_hash as refundHash,
                        jj.prize_taken_hash as prizeTakenHash, 
                        jj.created_date as createdDate,
                        jj.modified_date as modifiedDate
                    from jackpot_join jj 
                    left join jackpot_p2p_bet jb on jj.jackpot_bet_id_ref = jb.id 
            )
            select * from union_joins
        """;
    }
}