package com.p2pbet.users.service

import com.p2pbet.users.entity.ClientBetJoinEntity
import com.p2pbet.users.entity.ClientBetTransactionEntity
import com.p2pbet.users.repository.ClientBetJoinRepository
import com.p2pbet.users.repository.ClientBetTransactionRepository
import com.p2pbet.users.rest.model.*
import com.p2pbet.users.rest.model.AggregatedClientStatusJoinResponse.Companion.toResponse
import com.p2pbet.users.service.converter.ClientConverterService
import com.p2pbet.util.filter.specification.SpecificationBuilder
import com.p2pbet.util.model.PageRequest.Companion.toDomainPageRequest
import com.p2pbet.util.model.PageResponse
import com.p2pbet.util.model.PageResponse.Companion.convertToPageResponse
import org.springframework.stereotype.Service

@Service
class ClientBetFilterService(
    val clientBetJoinRepository: ClientBetJoinRepository,
    val clientBetTransactionRepository: ClientBetTransactionRepository,
    val clientConverterService: ClientConverterService,
) {
    fun getSummaryInfo(address: String): List<AggregatedClientStatusJoinResponse> =
        clientBetJoinRepository.getAggregatedJoinByStatus(
            clientAddress = address
        ).map {
            it.toResponse()
        }

    fun getTransactions(requestFilter: ClientTransactionPageableRequest): PageResponse<ClientTransactionResponse> {
        val spec = SpecificationBuilder<ClientBetTransactionEntity>().with(requestFilter, listOf()).build()

        val pageRequest = requestFilter.toDomainPageRequest()

        return clientBetTransactionRepository.findAll(spec, pageRequest)
            .map(clientConverterService::convertToTransactionResponse)
            .convertToPageResponse()
    }

    fun getJoins(requestFilter: ClientJoinPageableRequest): PageResponse<ClientJoinResponse> {
        val spec = SpecificationBuilder<ClientBetJoinEntity>().with(requestFilter, listOf()).build()

        val pageRequest = requestFilter.toDomainPageRequest()

        return clientBetJoinRepository.findAll(spec, pageRequest)
            .map(clientConverterService::convertToJoinResponse)
            .convertToPageResponse()
    }
}