package com.p2pbet.bet.jackpot.repository

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.jackpot.entity.JackpotJoinEntity
import com.p2pbet.bet.jackpot.entity.JackpotP2PBetEntity
import com.p2pbet.bet.jackpot.entity.enums.JackpotJoinStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface JackpotJoinRepository : JpaRepository<JackpotJoinEntity, UUID>,
    JpaSpecificationExecutor<JackpotJoinEntity> {
    fun findFirstByJoinIdClientRefAndClientAndJackpotBetBetIdAndExecutionType(
        joinIdClientRef: Long,
        client: String,
        betId: Long,
        executionType: BetExecutionType,
    ): JackpotJoinEntity

    fun findAllByClientAndJackpotBetBetIdAndExecutionTypeAndStatus(
        client: String,
        betId: Long,
        executionType: BetExecutionType,
        status: JackpotJoinStatus,
    ): List<JackpotJoinEntity>

    fun findAllByJackpotBetBetIdAndStatusIn(
        betId: Long,
        status: Set<JackpotJoinStatus>,
    ): List<JackpotJoinEntity>

    fun findAllByJackpotBetBetIdAndExecutionTypeAndClientAndStatusIn(
        betId: Long,
        executionType: BetExecutionType,
        client: String,
        status: List<JackpotJoinStatus>,
    ): List<JackpotJoinEntity>

    fun existsByJackpotBetBetIdAndExecutionTypeAndStatus(
        betId: Long,
        executionType: BetExecutionType,
        status: JackpotJoinStatus,
    ): Boolean

    @Query(
        value = """
            select au.* from jackpot_join au
                left join (select id, abs(cast(target_value as numeric) - cast( ?1 as numeric)) as diff from jackpot_join where jackpot_bet_id = ?2 and execution_type = ?3) joinWon 
                    on au.id = joinWon.id 
                where not au.status = 'CANCELED' and not joinWon.id is null
            order by joinWon.diff asc 
            limit 1
        """,
        nativeQuery = true
    )
    fun findLeader(
        value: String,
        betId: Long,
        executionType: String,
    ): JackpotJoinEntity?

    @Modifying
    @Query(
        value = """
        update jackpot_join set status=?1, modified_date = ?2
        where jackpot_bet_id = ?3 and
        trunc(cast(target_value as numeric), ?4) = trunc(cast(?5 as numeric), ?4) and
        status in ?6 and execution_type = ?7
    """, nativeQuery = true
    )
    fun updateJoinsToWon(
        resultStatus: String,
        date: LocalDateTime,
        betId: Long,
        scale: Int,
        finalValue: String,
        statuses: List<String>,
        executionType: String,
    ): Int

    @Modifying
    @Query("update JackpotJoinEntity j set j.status = 'LOST', j.modifiedDate = :date where j.jackpotBet = :bet and j.status = :status and j.executionType = :executionType")
    fun updateOtherJoinsToLost(
        @Param("date") date: LocalDateTime,
        @Param("bet") bet: JackpotP2PBetEntity,
        @Param("status") status: JackpotJoinStatus,
        @Param("executionType") executionType: BetExecutionType,
    ): Int
}
