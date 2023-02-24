package com.p2pbet.bet.custom.service

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.custom.entity.CustomJoinEntity
import com.p2pbet.bet.custom.entity.CustomP2PBetEntity
import com.p2pbet.bet.custom.repository.CustomJoinRepository
import com.p2pbet.bet.custom.repository.CustomP2PBetRepository
import com.p2pbet.bet.custom.repository.model.AggregationByEventCustom
import com.p2pbet.bet.custom.rest.model.CustomBetPageableWithFilterRequest
import com.p2pbet.bet.custom.rest.model.CustomBetResponse
import com.p2pbet.bet.custom.rest.model.CustomBetResponse.Companion.toCustomBetResponse
import com.p2pbet.bet.custom.rest.model.CustomJoinResponse
import com.p2pbet.bet.custom.rest.model.CustomJoinResponse.Companion.toCustomJoinResponse
import com.p2pbet.bet.custom.rest.model.CustomJoinsPageableWithFilterRequest
import com.p2pbet.util.filter.specification.SpecificationBuilder
import com.p2pbet.util.model.PageRequest.Companion.toDomainPageRequest
import com.p2pbet.util.model.PageResponse
import com.p2pbet.util.model.PageResponse.Companion.convertToPageResponse
import org.springframework.stereotype.Service

@Service
class CustomBetFilterService(
    val customP2PBetRepository: CustomP2PBetRepository,
    val customJoinRepository: CustomJoinRepository,
) {
    fun getCustomBetsWithFilters(requestFilter: CustomBetPageableWithFilterRequest): PageResponse<CustomBetResponse> {
        val spec = SpecificationBuilder<CustomP2PBetEntity>().with(requestFilter, listOf()).build()

        val pageRequest = requestFilter.toDomainPageRequest()

        return customP2PBetRepository.findAll(spec, pageRequest)
            .map {
                it.toCustomBetResponse()
            }.convertToPageResponse()
    }

    fun getCustomBet(customId: Long, executionType: BetExecutionType) =
        customP2PBetRepository
            .findFirstByBetIdAndBaseBetExecutionType(customId, executionType)
            .toCustomBetResponse()

    fun getCustomJoinsWithFilters(requestFilter: CustomJoinsPageableWithFilterRequest): PageResponse<CustomJoinResponse> {
        val spec = SpecificationBuilder<CustomJoinEntity>().with(requestFilter, listOf()).build()

        val pageRequest = requestFilter.toDomainPageRequest()

        return customJoinRepository.findAll(spec, pageRequest)
            .map {
                it.toCustomJoinResponse()
            }.convertToPageResponse()
    }

    fun getAggregateByEvent(executionType: BetExecutionType): List<AggregationByEventCustom> =
        customP2PBetRepository.getTotalAggregateByEvent(
            executionType = executionType.name
        )
}