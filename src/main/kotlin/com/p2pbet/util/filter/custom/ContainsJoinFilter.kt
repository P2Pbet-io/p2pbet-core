package com.p2pbet.util.filter.custom

import com.p2pbet.bet.auction.entity.AuctionJoinEntity
import com.p2pbet.bet.auction.entity.AuctionP2PBetEntity
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.specification.GenericSpecification
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.JoinType

class ContainsJoinFilter(
    val period: Long,
    val condition: Condition,
) {
    companion object {
        fun String.toCriteriaAuctionJoinRequest(): Specification<AuctionP2PBetEntity> =
            Specification { root, query, criteriaBuilder ->
                val join = root.join<AuctionP2PBetEntity, AuctionJoinEntity>(
                    "joins", JoinType.LEFT
                )
                query.distinct(true)
                join.on(
                    criteriaBuilder.equal(
                        GenericSpecification.extractPath<Any>(join, "client"),
                        this
                    )
                ).on
            }

    }
}