package com.p2pbet.bet.auction.repository

import com.p2pbet.bet.auction.entity.AuctionP2PBetEntity
import com.p2pbet.bet.auction.repository.model.AggregatedPoolByAuctionBet
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AuctionP2PBetRepository : JpaRepository<AuctionP2PBetEntity, UUID>,
    JpaSpecificationExecutor<AuctionP2PBetEntity> {

    fun findFirstByBetIdAndBaseBetExecutionType(
        betId: Long,
        executionType: BetExecutionType,
    ): AuctionP2PBetEntity

    @Query(
        value = """
        select aj.auction_bet_id as auctionBetId, sum(ab.request_amount) as totalPool from auction_join aj
        left join auction_p2p_bet ab on aj.auction_bet_id_ref = ab.id
            where not aj.status = 'CANCELED' and aj.auction_bet_id in ?1
        group by aj.auction_bet_id 
    """, nativeQuery = true
    )
    fun getAggregatedPoolByAuctionBets(customBetIds: List<Long>): List<AggregatedPoolByAuctionBet>
}