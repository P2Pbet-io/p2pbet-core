package com.p2pbet.bet.jackpot.service

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.jackpot.entity.JackpotJoinEntity
import com.p2pbet.bet.jackpot.entity.JackpotP2PBetEntity
import com.p2pbet.bet.jackpot.repository.JackpotJoinRepository
import com.p2pbet.bet.jackpot.repository.JackpotP2PBetRepository
import com.p2pbet.bet.jackpot.rest.model.JackpotBetPageableWithFilterRequest
import com.p2pbet.bet.jackpot.rest.model.JackpotBetResponse
import com.p2pbet.bet.jackpot.rest.model.JackpotBetResponse.Companion.toResponse
import com.p2pbet.bet.jackpot.rest.model.JackpotJoinResponse
import com.p2pbet.bet.jackpot.rest.model.JackpotJoinResponse.Companion.toJackpotJoinResponse
import com.p2pbet.bet.jackpot.rest.model.JackpotJoinsPageableWithFilterRequest
import com.p2pbet.util.filter.specification.SpecificationBuilder
import com.p2pbet.util.model.PageRequest.Companion.toDomainPageRequest
import com.p2pbet.util.model.PageResponse
import com.p2pbet.util.model.PageResponse.Companion.convertToPageResponse
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class JackpotBetFilterService(
    val jackpotP2PBetRepository: JackpotP2PBetRepository,
    val jackpotJoinRepository: JackpotJoinRepository,
) {
    fun getJackpotBetsWithFilters(requestFilter: JackpotBetPageableWithFilterRequest): PageResponse<JackpotBetResponse> {
        val spec = SpecificationBuilder<JackpotP2PBetEntity>().with(requestFilter, listOf()).build()

        val pageRequest = requestFilter.toDomainPageRequest()

        return jackpotP2PBetRepository.findAll(spec, pageRequest)
            .map {
                it.toResponse()
            }.convertToPageResponse()
    }

    fun getJackpotBet(betId: Long, executionType: BetExecutionType) =
        jackpotP2PBetRepository.findFirstByBetIdAndBaseBetExecutionType(betId, executionType).toResponse()

    fun getLeader(betId: Long, target: String, executionType: BetExecutionType): JackpotJoinResponse =
        jackpotJoinRepository.findLeader(
            betId = betId,
            value = target,
            executionType = executionType.name
        )?.toJackpotJoinResponse()
            ?: throw EntityNotFoundException("There is no leader")

    fun getJackpotJoinsWithFilters(requestFilter: JackpotJoinsPageableWithFilterRequest): PageResponse<JackpotJoinResponse> {
        val spec = SpecificationBuilder<JackpotJoinEntity>().with(requestFilter, listOf()).build()

        val pageRequest = requestFilter.toDomainPageRequest()

        return jackpotJoinRepository.findAll(spec, pageRequest)
            .map {
                it.toJackpotJoinResponse()
            }.convertToPageResponse()
    }
}