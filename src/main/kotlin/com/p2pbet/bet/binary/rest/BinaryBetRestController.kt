package com.p2pbet.bet.binary.rest

import com.p2pbet.bet.binary.rest.model.BinaryBetPageableWithFilterRequest
import com.p2pbet.bet.binary.rest.model.BinaryBetResponse
import com.p2pbet.bet.binary.rest.model.BinaryJoinResponse
import com.p2pbet.bet.binary.rest.model.BinaryJoinsPageableWithFilterRequest
import com.p2pbet.bet.binary.service.BinaryBetFilterService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.util.model.PageResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/public/api/v1/binary/bet")
class BinaryBetRestController(
    val binaryBetFilterService: BinaryBetFilterService,
) {

    @GetMapping("/{executionType}/{id}")
    fun getBinaryBet(@PathVariable executionType: BetExecutionType, @PathVariable id: Long): BinaryBetResponse =
        binaryBetFilterService.getBinaryBet(id, executionType)

    @PostMapping
    fun getBinaryWithFilters(
        @RequestBody request: BinaryBetPageableWithFilterRequest,
    ): PageResponse<BinaryBetResponse> = binaryBetFilterService.getBinaryBetWithFilters(request)

    @PostMapping("/joins")
    fun getCustomJoinsWithFilters(
        @RequestBody request: BinaryJoinsPageableWithFilterRequest,
    ): PageResponse<BinaryJoinResponse> = binaryBetFilterService.getBinaryJoinsWithFilters(request)
}