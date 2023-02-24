package com.p2pbet.p2pevent.controller

import com.p2pbet.p2pevent.controller.model.AddEventRequest
import com.p2pbet.p2pevent.service.EventService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/events")
class EventController(
    private val eventService: EventService
) {

    @PostMapping("/event-type")
    fun add(@RequestBody request: AddEventRequest) = eventService.add(request)
}