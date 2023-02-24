package com.p2pbet.bet.binary.repository

import com.p2pbet.bet.binary.entity.BinaryJoinEntity
import com.p2pbet.bet.binary.entity.BinaryP2PBetEntity
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface BinaryJoinRepository : JpaRepository<BinaryJoinEntity, UUID>, JpaSpecificationExecutor<BinaryJoinEntity> {
    fun findFirstByJoinIdClientRefAndClientAndBinaryBetBetIdAndExecutionType(
        joinIdClientRef: Long,
        client: String,
        betId: Long,
        executionType: BetExecutionType,
    ): BinaryJoinEntity

    fun findAllByClientAndBinaryBetBetIdAndStatusAndExecutionType(
        client: String,
        betId: Long,
        status: JoinStatus,
        executionType: BetExecutionType,
    ): List<BinaryJoinEntity>

    fun existsByBinaryBetBetIdAndStatusAndExecutionType(
        betId: Long,
        status: JoinStatus,
        executionType: BetExecutionType,
    ): Boolean

    fun findAllByBinaryBetBetIdAndStatusIn(
        betId: Long,
        status: Set<JoinStatus>,
    ): List<BinaryJoinEntity>

    @Modifying
    @Query("update BinaryJoinEntity j set j.status = :newStatus, j.modifiedDate = :date where j.binaryBet = :bet and j.status = :status and j.side = :wonSide and j.executionType = :executionType")
    fun updateJoinsToStatus(
        @Param("newStatus") newStatus: JoinStatus,
        @Param("date") date: LocalDateTime,
        @Param("bet") bet: BinaryP2PBetEntity,
        @Param("wonSide") wonSide: Boolean,
        @Param("status") status: JoinStatus,
        @Param("executionType") executionType: BetExecutionType,
    )
}
