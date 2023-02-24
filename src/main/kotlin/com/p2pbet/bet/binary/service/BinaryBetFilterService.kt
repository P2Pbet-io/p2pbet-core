package com.p2pbet.bet.binary.service

import com.p2pbet.bet.binary.entity.BinaryJoinEntity
import com.p2pbet.bet.binary.entity.BinaryP2PBetEntity
import com.p2pbet.bet.binary.repository.BinaryJoinRepository
import com.p2pbet.bet.binary.repository.BinaryP2PBetRepository
import com.p2pbet.bet.binary.rest.model.BinaryBetPageableWithFilterRequest
import com.p2pbet.bet.binary.rest.model.BinaryBetResponse
import com.p2pbet.bet.binary.rest.model.BinaryBetResponse.Companion.toResponse
import com.p2pbet.bet.binary.rest.model.BinaryJoinResponse
import com.p2pbet.bet.binary.rest.model.BinaryJoinResponse.Companion.toBinaryJoinResponse
import com.p2pbet.bet.binary.rest.model.BinaryJoinsPageableWithFilterRequest
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.util.filter.custom.PeriodFilter.Companion.toCriteriaRequest
import com.p2pbet.util.filter.specification.SpecificationBuilder
import com.p2pbet.util.model.PageRequest.Companion.toDomainPageRequest
import com.p2pbet.util.model.PageResponse
import com.p2pbet.util.model.PageResponse.Companion.convertToPageResponse
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class BinaryBetFilterService(
    val binaryP2PBetRepository: BinaryP2PBetRepository,
    val binaryJoinRepository: BinaryJoinRepository,
) {
    fun getBinaryBetWithFilters(requestFilter: BinaryBetPageableWithFilterRequest): PageResponse<BinaryBetResponse> {
        val periodSpec = requestFilter.periodFilter?.toCriteriaRequest<BinaryP2PBetEntity>(
            "baseBet.expirationDate",
            "baseBet.lockDate"
        )

        val spec = SpecificationBuilder<BinaryP2PBetEntity>().with(requestFilter, listOf()).build()?.and(periodSpec)
            ?: periodSpec

        val pageRequest = requestFilter.toDomainPageRequest()

        return binaryP2PBetRepository.findAll(spec, pageRequest)
            .map {
                it.toResponse()
            }
            .enrichmentWithPool(requestFilter.executionType)
            .convertToPageResponse()
    }

    private fun <T : Iterable<BinaryBetResponse>> T.enrichmentWithPool(executionType: BetExecutionType): T = with(
        binaryP2PBetRepository.getAggregatedPoolByBinaryBets(
            binaryBetIds = this.map(BinaryBetResponse::id),
            executionType = executionType.name
        ).associate { (it.binaryBetId to it.side) to (it.pool to it.count) }
    ) {
        this@enrichmentWithPool.onEach { binaryBetResponse ->
            binaryBetResponse.leftPool = this[binaryBetResponse.id to true]?.first ?: BigDecimal.ZERO
            binaryBetResponse.leftCount = this[binaryBetResponse.id to true]?.second ?: 0L
            binaryBetResponse.rightPool = this[binaryBetResponse.id to false]?.first ?: BigDecimal.ZERO
            binaryBetResponse.rightCount = this[binaryBetResponse.id to false]?.second ?: 0L
        }
        this@enrichmentWithPool
    }

    fun getBinaryBet(id: Long, executionType: BetExecutionType): BinaryBetResponse =
        binaryP2PBetRepository
            .findFirstByBetIdAndBaseBetExecutionType(id, executionType)
            .toResponse()
            .apply {
                val enrichmentMap = binaryP2PBetRepository.getAggregatedPoolByBinaryBets(
                    binaryBetIds = listOf(id),
                    executionType = executionType.name
                ).associate { (it.binaryBetId to it.side) to it.pool }
                leftPool = enrichmentMap[id to true] ?: BigDecimal.ZERO
                rightPool = enrichmentMap[id to false] ?: BigDecimal.ZERO
            }

    fun getBinaryJoinsWithFilters(requestFilter: BinaryJoinsPageableWithFilterRequest): PageResponse<BinaryJoinResponse> {
        val spec = SpecificationBuilder<BinaryJoinEntity>().with(requestFilter, listOf()).build()

        val pageRequest = requestFilter.toDomainPageRequest()

        return binaryJoinRepository.findAll(spec, pageRequest)
            .map {
                it.toBinaryJoinResponse()
            }.convertToPageResponse()
    }
}