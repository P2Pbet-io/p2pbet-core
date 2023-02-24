package com.p2pbet.p2pevent.controller

import com.p2pbet.p2pevent.controller.model.BaseEventWithPopularityResponse
import com.p2pbet.p2pevent.controller.model.EventType
import com.p2pbet.p2pevent.controller.model.EventWithPopularityPageRequest
import com.p2pbet.p2pevent.entity.BaseEvent
import com.p2pbet.p2pevent.service.EventService
import com.p2pbet.util.model.PageResponse
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/public/api/v1/events")
class PublicEventController(
    private val eventService: EventService,
) {
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: UUID): BaseEvent = eventService.getOne(id)

    @GetMapping("/types")
    fun getTypes(): List<EventType> = EventType.values().toList()

    @PostMapping
    fun getPageable(@RequestBody eventWithPopularityPageRequest: EventWithPopularityPageRequest): PageResponse<BaseEventWithPopularityResponse> =
        eventService.getWithPopularityPagination(eventWithPopularityPageRequest)
}