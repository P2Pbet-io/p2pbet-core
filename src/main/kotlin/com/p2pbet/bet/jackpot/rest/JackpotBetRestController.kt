package com.p2pbet.bet.jackpot.rest

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.jackpot.rest.model.JackpotBetPageableWithFilterRequest
import com.p2pbet.bet.jackpot.rest.model.JackpotBetResponse
import com.p2pbet.bet.jackpot.rest.model.JackpotJoinResponse
import com.p2pbet.bet.jackpot.rest.model.JackpotJoinsPageableWithFilterRequest
import com.p2pbet.bet.jackpot.service.JackpotBetFilterService
import com.p2pbet.util.model.PageResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/public/api/v1/jackpot/bet")
class JackpotBetRestController(
    val jackpotBetFilterService: JackpotBetFilterService,
) {
    @GetMapping("/{executionType}/{id}")
    fun getJackpotBet(@PathVariable executionType: BetExecutionType, @PathVariable id: Long): JackpotBetResponse =
        jackpotBetFilterService.getJackpotBet(id, executionType)

    @PostMapping
    fun getJackpotWithFilters(
        @RequestBody request: JackpotBetPageableWithFilterRequest,
    ): PageResponse<JackpotBetResponse> = jackpotBetFilterService.getJackpotBetsWithFilters(request)

    @PostMapping("/joins")
    fun getJackpotJoinsWithFilters(
        @RequestBody request: JackpotJoinsPageableWithFilterRequest,
    ): PageResponse<JackpotJoinResponse> = jackpotBetFilterService.getJackpotJoinsWithFilters(request)

    @GetMapping("/{executionType}/{id}/leader")
    fun getLeaderJoin(
        @PathVariable executionType: BetExecutionType,
        @PathVariable id: Long,
        @RequestParam value: String,
    ): JackpotJoinResponse =
        jackpotBetFilterService.getLeader(betId = id, target = value, executionType = executionType)

}