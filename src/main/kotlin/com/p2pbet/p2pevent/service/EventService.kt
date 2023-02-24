package com.p2pbet.p2pevent.service

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.custom.service.CustomBetFilterService
import com.p2pbet.finnhub.FinnHubService
import com.p2pbet.p2pevent.controller.model.AddEventRequest
import com.p2pbet.p2pevent.controller.model.BaseEventWithPopularityResponse
import com.p2pbet.p2pevent.controller.model.EventWithPopularityPageRequest
import com.p2pbet.p2pevent.entity.BaseEvent
import com.p2pbet.p2pevent.repository.EventRepository
import com.p2pbet.util.model.PageRequest.Companion.toDomainPageRequest
import com.p2pbet.util.model.PageResponse.Companion.convertToPageResponseWithTransform
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val finnHubService: FinnHubService,
    private val customBetFilterService: CustomBetFilterService,
) {
    fun add(request: AddEventRequest) {
        eventRepository.save(request.toEntity())
    }

    fun getOne(id: UUID): BaseEvent =
        eventRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("BaseEvent with $id has not been found")

    fun getWithPopularityPagination(eventWithPopularityPageRequest: EventWithPopularityPageRequest) =
        eventRepository.findAll(
            eventWithPopularityPageRequest.toDomainPageRequest()
        ).convertToPageResponseWithTransform {
            convertToPopularityEvent(it, eventWithPopularityPageRequest.executionType)
        }


    fun getValueByDate(id: UUID, date: LocalDateTime): BigDecimal = getOne(id)
        .let { finnHubService.getPriceByDate(it.type, it.symbol, date) }
        .setScale(2, RoundingMode.HALF_DOWN)

    private fun convertToPopularityEvent(
        source: List<BaseEvent>,
        executionType: BetExecutionType,
    ): List<BaseEventWithPopularityResponse> = with(
        customBetFilterService.getAggregateByEvent(
            executionType = executionType
        ).associateBy { UUID.fromString(it.eventBaseId) }
    ) {
        source.map {
            BaseEventWithPopularityResponse(
                event = it,
                finBetCount = this[it.id]?.count ?: 0,
                finBetPool = this[it.id]?.sum ?: BigDecimal.ZERO,
            )
        }
    }

    companion object {
        private fun AddEventRequest.toEntity() = BaseEvent(
            name = name,
            symbol = symbol,
            type = type,
            fullName = fullName,
            srcUrl = srcUrl
        )
    }
}