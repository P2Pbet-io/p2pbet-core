package com.p2pbet.bet.common.rest

import com.p2pbet.bet.common.service.BetSchedulerService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reschedule")
class SchedulerController(
    private val betSchedulerService: BetSchedulerService,
) {

    @PostMapping
    fun reschedule() = betSchedulerService.reschedule()
}