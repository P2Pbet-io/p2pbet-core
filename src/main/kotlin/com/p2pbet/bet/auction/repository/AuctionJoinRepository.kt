package com.p2pbet.bet.auction.repository

import com.p2pbet.bet.auction.entity.AuctionJoinEntity
import com.p2pbet.bet.auction.entity.AuctionP2PBetEntity
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
interface AuctionJoinRepository : JpaRepository<AuctionJoinEntity, UUID>, JpaSpecificationExecutor<AuctionJoinEntity> {
    fun findFirstByJoinIdClientRefAndClientAndAuctionBetBetIdAndExecutionType(
        joinIdClientRef: Long,
        client: String,
        betId: Long,
        executionType: BetExecutionType,
    ): AuctionJoinEntity

    fun findFirstByAuctionBetBetIdAndClientAndStatusAndExecutionType(
        betId: Long,
        client: String,
        status: JoinStatus,
        executionType: BetExecutionType,
    ): AuctionJoinEntity

    fun findAllByClientAndAuctionBetBetIdAndStatusAndExecutionType(
        client: String,
        betId: Long,
        status: JoinStatus,
        executionType: BetExecutionType,
    ): List<AuctionJoinEntity>

    fun findAllByClientAndAuctionBetBetIdAndExecutionType(
        client: String,
        betId: Long,
        executionType: BetExecutionType,
    ): List<AuctionJoinEntity>

    fun existsByAuctionBetBetIdAndStatusAndExecutionType(
        betId: Long,
        status: JoinStatus,
        executionType: BetExecutionType,
    ): Boolean

    fun findAllByAuctionBetBetIdAndStatusAndExecutionType(
        betId: Long,
        status: JoinStatus,
        executionType: BetExecutionType,
    ): List<AuctionJoinEntity>

    fun findAllByAuctionBetBetIdAndStatusIn(
        betId: Long,
        status: Set<JoinStatus>
    ): List<AuctionJoinEntity>

    @Query(
        value = """
            select au.* from auction_join au
                left join (select id, abs(cast(target_value as numeric) - cast( ?1 as numeric)) as diff from auction_join where auction_bet_id = ?2 and execution_type = ?3) joinWon 
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
    ): AuctionJoinEntity?

    @Query(
        value = """
            select au.* from auction_join au
                left join (select id, abs(cast(target_value as numeric) - cast( ?1 as numeric)) as diff from auction_join where auction_bet_id = ?2 and execution_type = ?3) joinWon 
                    on au.id = joinWon.id 
                where not au.status = 'CANCELED' and not joinWon.id is null
            order by joinWon.diff asc 
        """,
        nativeQuery = true
    )
    fun getTotalOrderByDiff(
        value: String,
        betId: Long,
        executionType: String,
    ): List<AuctionJoinEntity>

    @Query(
        value = """
            select * from auction_join where 
                auction_bet_id  = ?2 and execution_type = ?4 and target_value = 
            (select au.target_value from auction_join au
                left join (select id, abs(cast(target_value as numeric) - cast( ?1 as numeric)) as diff from auction_join where auction_bet_id = ?2 and execution_type = ?4) joinWon 
                    on au.id = joinWon.id 
                where au.status = ?3
            order by joinWon.diff asc 
            limit 1)
        """,
        nativeQuery = true
    )
    fun findWonJoin(
        finalValue: String,
        betId: Long,
        joinStatus: String,
        executionType: String,
    ): List<AuctionJoinEntity>

    @Modifying
    @Query("update AuctionJoinEntity j set j.status = 'LOST', j.modifiedDate = :date where j.auctionBet = :bet and not j.id in :wonIds and j.status = :status and j.executionType = :executionType")
    fun updateOtherJoinsToLost(
        @Param("date") date: LocalDateTime,
        @Param("bet") bet: AuctionP2PBetEntity,
        @Param("wonIds") wonIds: List<UUID>,
        @Param("status") status: JoinStatus,
        @Param("executionType") executionType: BetExecutionType,
    )
}
