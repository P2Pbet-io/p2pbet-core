package com.p2pbet.bet.custom.rest

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.custom.rest.model.CustomBetPageableWithFilterRequest
import com.p2pbet.bet.custom.rest.model.CustomBetResponse
import com.p2pbet.bet.custom.rest.model.CustomJoinResponse
import com.p2pbet.bet.custom.rest.model.CustomJoinsPageableWithFilterRequest
import com.p2pbet.bet.custom.service.CustomBetFilterService
import com.p2pbet.util.model.PageResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/public/api/v1/custom/bet")
class CustomBetRestController(
    val customBetFilterService: CustomBetFilterService,
) {

    @GetMapping("/{executionType}/{id}")
    fun getCustomBet(@PathVariable executionType: BetExecutionType, @PathVariable id: Long): CustomBetResponse =
        customBetFilterService.getCustomBet(id, executionType)

    @PostMapping
    fun getCustomBetsWithFilters(
        @RequestBody request: CustomBetPageableWithFilterRequest,
    ): PageResponse<CustomBetResponse> = customBetFilterService.getCustomBetsWithFilters(request)

    @PostMapping("/joins")
    fun getCustomJoinsWithFilters(
        @RequestBody request: CustomJoinsPageableWithFilterRequest,
    ): PageResponse<CustomJoinResponse> = customBetFilterService.getCustomJoinsWithFilters(request)
}