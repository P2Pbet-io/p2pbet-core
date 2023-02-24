package com.p2pbet.bet.auction.service

import com.p2pbet.bet.auction.entity.AuctionJoinEntity
import com.p2pbet.bet.auction.entity.AuctionP2PBetEntity
import com.p2pbet.bet.auction.repository.AuctionJoinRepository
import com.p2pbet.bet.auction.repository.AuctionP2PBetRepository
import com.p2pbet.bet.auction.rest.model.*
import com.p2pbet.bet.auction.rest.model.AuctionBetResponse.Companion.toResponse
import com.p2pbet.bet.auction.rest.model.AuctionJoinResponse.Companion.getAggregatedStatus
import com.p2pbet.bet.auction.rest.model.AuctionJoinResponse.Companion.toAuctionJoinResponse
import com.p2pbet.bet.auction.rest.model.PersonalAuctionBetResponse.Companion.toPersonalResponse
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.service.ReportBuilder
import com.p2pbet.util.filter.custom.ContainsJoinFilter.Companion.toCriteriaAuctionJoinRequest
import com.p2pbet.util.filter.custom.PeriodFilter.Companion.toCriteriaRequest
import com.p2pbet.util.filter.specification.SpecificationBuilder
import com.p2pbet.util.model.PageRequest.Companion.toDomainPageRequest
import com.p2pbet.util.model.PageResponse
import com.p2pbet.util.model.PageResponse.Companion.convertToPageResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayInputStream
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException

@Service
class AuctionBetFilterService(
    val auctionP2PBetRepository: AuctionP2PBetRepository,
    val auctionJoinRepository: AuctionJoinRepository,
) {
    @Transactional
    fun getWithFilters(requestFilter: AuctionBetPageableWithFilterRequest): PageResponse<AuctionBetResponse> {
        val periodSpec = requestFilter.periodFilter?.toCriteriaRequest<AuctionP2PBetEntity>(
            "baseBet.expirationDate",
            "baseBet.lockDate"
        )

        val containsRequest = requestFilter.clientAddress?.toCriteriaAuctionJoinRequest()

        val spec = (SpecificationBuilder<AuctionP2PBetEntity>().with(requestFilter, listOf()).build()?.and(periodSpec)
            ?: periodSpec
                )?.and(containsRequest)
            ?: containsRequest


        val pageRequest = requestFilter.toDomainPageRequest()

        return auctionP2PBetRepository.findAll(spec, pageRequest)
            .map {
                it.toResponse()
            }
            .enrichmentWithPool()
            .convertToPageResponse()
    }

    fun getLeader(betId: Long, target: String, executionType: BetExecutionType): AuctionJoinResponse =
        auctionJoinRepository.findLeader(
            betId = betId,
            value = target,
            executionType = executionType.name
        )?.toAuctionJoinResponse()
            ?: throw EntityNotFoundException("There is no leader")

    fun formReport(betId: Long, target: String, executionType: BetExecutionType): ByteArrayInputStream {
        val bet = auctionP2PBetRepository.findFirstByBetIdAndBaseBetExecutionType(betId, executionType)

        val betTarget = bet.finalValue ?: target

        val list = auctionJoinRepository.getTotalOrderByDiff(
            betId = betId,
            value = betTarget,
            executionType = executionType.name
        )

        val finalReport: Boolean
        val reportDate = if (bet.baseBet.expirationDate > LocalDateTime.now()) {
            finalReport = false
            LocalDateTime.now()
        } else {
            finalReport = true
            bet.baseBet.expirationDate
        }

        return ByteArrayInputStream(
            ReportBuilder.buildActionJoinReport(
                data = list,
                target = betTarget,
                reportDate = reportDate,
                finalReport = finalReport
            ).toByteArray()
        )
    }

    @Transactional
    fun getWithFilters(requestFilter: AuctionPersonalBetPageableWithFilterRequest): PageResponse<PersonalAuctionBetResponse> {
        val periodSpec = requestFilter.periodFilter?.toCriteriaRequest<AuctionP2PBetEntity>(
            "baseBet.expirationDate",
            "baseBet.lockDate"
        )

        val containsRequest = requestFilter.clientAddress.toCriteriaAuctionJoinRequest()

        val spec = (SpecificationBuilder<AuctionP2PBetEntity>().with(requestFilter, listOf()).build()?.and(periodSpec)
            ?: periodSpec
                )?.and(containsRequest)
            ?: containsRequest

        val pageRequest = requestFilter.toDomainPageRequest()

        return auctionP2PBetRepository.findAll(spec, pageRequest)
            .map {
                it.toPersonalResponse()
            }
            .enrichmentWithPool()
            .enrichmentPersonalWithPool(requestFilter.clientAddress, requestFilter.executionType)
            .convertToPageResponse()
    }


    private fun <T : Iterable<AuctionBetResponse>> T.enrichmentWithPool(): T = with(
        auctionP2PBetRepository.getAggregatedPoolByAuctionBets(
            customBetIds = this.map(AuctionBetResponse::id)
        ).associate { it.auctionBetId to it.totalPool }
    ) {
        this@enrichmentWithPool.onEach { auctionBetResponse ->
            auctionBetResponse.totalPool = this[auctionBetResponse.id] ?: BigDecimal.ZERO
        }
        this@enrichmentWithPool
    }

    private fun <T : Iterable<PersonalAuctionBetResponse>> T.enrichmentPersonalWithPool(
        client: String,
        executionType: BetExecutionType,
    ): T =
        this.onEach {
            it.personalJoins = auctionJoinRepository.findAllByClientAndAuctionBetBetIdAndExecutionType(
                client = client,
                betId = it.id,
                executionType = executionType
            ).map { join ->
                join.toAuctionJoinResponse()
            }
            it.personalAggregatedStatus = it.personalJoins.getAggregatedStatus()
        }


    fun getAuctionBet(id: Long, executionType: BetExecutionType): AuctionBetResponse =
        auctionP2PBetRepository.findFirstByBetIdAndBaseBetExecutionType(id, executionType).toResponse()
            .apply {
                totalPool = auctionP2PBetRepository.getAggregatedPoolByAuctionBets(
                    customBetIds = listOf(id)
                ).associate { it.auctionBetId to it.totalPool }[id] ?: BigDecimal.ZERO
            }

    fun getAuctionJoinsWithFilters(requestFilter: AuctionJoinsPageableWithFilterRequest): PageResponse<AuctionJoinResponse> {
        val periodSpec = requestFilter.periodFilter?.toCriteriaRequest<AuctionJoinEntity>(
            "auctionBet.baseBet.expirationDate",
            "auctionBet.baseBet.lockDate"
        )

        val spec = SpecificationBuilder<AuctionJoinEntity>().with(requestFilter, listOf()).build()?.and(periodSpec)
            ?: periodSpec

        val pageRequest = requestFilter.toDomainPageRequest()

        return auctionJoinRepository.findAll(spec, pageRequest)
            .map {
                it.toAuctionJoinResponse()
            }.convertToPageResponse()
    }
}