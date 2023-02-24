package com.p2pbet.bet.common.service

import com.p2pbet.bet.common.repository.UnionRepository
import com.p2pbet.bet.common.rest.model.UnionJoinRequest
import com.p2pbet.bet.common.rest.model.UnionJoinResponse
import com.p2pbet.bet.common.rest.model.UnionJoinResponse.Companion.toResponse
import com.p2pbet.util.model.PageRequest.Companion.toDomainPageRequest
import com.p2pbet.util.model.PageResponse
import com.p2pbet.util.model.PageResponse.Companion.convertToPageResponse
import org.springframework.stereotype.Service

@Service
class UnionJoinFilterService(
    val unionRepository: UnionRepository,
) {
    fun getUnionJoinWithFilters(requestFilter: UnionJoinRequest): PageResponse<UnionJoinResponse> =
        unionRepository.getAggregatedUnionJoin(
            pageable = requestFilter.toDomainPageRequest()
        ).map {
            it.toResponse()
        }.convertToPageResponse()
}