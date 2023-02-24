package com.p2pbet.bet.custom.repository

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.custom.entity.CustomP2PBetEntity
import com.p2pbet.bet.custom.repository.model.AggregationByEventCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface CustomP2PBetRepository : JpaRepository<CustomP2PBetEntity, UUID>,
    JpaSpecificationExecutor<CustomP2PBetEntity> {

    fun findFirstByBetIdAndBaseBetExecutionType(
        betId: Long,
        executionType: BetExecutionType,
    ): CustomP2PBetEntity

    @Query(
        value = """
                with
                    eventBet as (
                        select cast(event_base_id as varchar) as id, count(*) as count 
                            from custom_p2p_bet
                            where lock_date > ?1 and execution_type = ?2
                        group by event_base_id
                    ),
                    eventJoin as (
                        select 
                        be.id as id,
                        sum(cj.join_amount) as sum
                            from base_event be 
                            left join custom_p2p_bet cpb on cpb.event_base_id = be.id 
                            left join custom_join cj on cj.custom_bet_id_ref  = cpb.id 
                            where cj.status = 'JOINED' and cpb.lock_date > ?1 and cpb.execution_type = ?2
                        group by be.id
                    )
                select
                 eb.id as eventBaseId,
                  coalesce(ej.sum, 0) as sum,
                  coalesce(eb.count, 0) as count from eventBet eb 
                left join eventJoin ej on cast(eb.id as text) = cast(ej.id as text)
                """,
        nativeQuery = true
    )
    fun getTotalAggregateByEvent(
        currentDate: LocalDateTime = LocalDateTime.now(),
        executionType: String,
    ): List<AggregationByEventCustom>
}