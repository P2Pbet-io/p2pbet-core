package com.p2pbet.bet.custom.repository

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.bet.custom.entity.CustomJoinEntity
import com.p2pbet.bet.custom.entity.CustomP2PBetEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CustomJoinRepository : JpaRepository<CustomJoinEntity, Long>,
    JpaSpecificationExecutor<CustomJoinEntity> {

    fun findFirstBySideAndCustomBetAndJoinId(
        side: Boolean,
        customBet: CustomP2PBetEntity,
        joinId: Long,
    ): CustomJoinEntity


    fun findFirstByJoinIdClientRefAndClientAndCustomBetBetIdAndExecutionType(
        joinIdClientRef: Long,
        client: String,
        betId: Long,
        executionType: BetExecutionType,
    ): CustomJoinEntity

    fun findAllByClientAndCustomBetBetIdAndStatusAndExecutionType(
        client: String,
        betId: Long,
        status: JoinStatus,
        executionType: BetExecutionType,
    ): List<CustomJoinEntity>

    fun existsByCustomBetBetIdAndStatusAndExecutionType(
        betId: Long,
        status: JoinStatus,
        executionType: BetExecutionType,
    ): Boolean

    fun findAllByCustomBetBetIdAndStatusIn(
        betId: Long,
        status: Set<JoinStatus>,
    ): List<CustomJoinEntity>

    @Modifying
    @Query("update CustomJoinEntity j set j.status = :newStatus, j.modifiedDate = :date where j.customBet = :bet and j.status = :status and j.side = :wonSide and j.executionType = :executionType")
    fun updateJoinsToStatus(
        @Param("newStatus") newStatus: JoinStatus,
        @Param("date") date: LocalDateTime,
        @Param("bet") bet: CustomP2PBetEntity,
        @Param("wonSide") wonSide: Boolean,
        @Param("status") status: JoinStatus,
        @Param("executionType") executionType: BetExecutionType,
    )
}
