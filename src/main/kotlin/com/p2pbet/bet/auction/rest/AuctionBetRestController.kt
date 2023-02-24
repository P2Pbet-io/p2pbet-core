package com.p2pbet.bet.auction.rest

import com.p2pbet.bet.auction.rest.model.*
import com.p2pbet.bet.auction.service.AuctionBetFilterService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.util.model.PageResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/public/api/v1/auction/bet")
class AuctionBetRestController(
    val auctionBetFilterService: AuctionBetFilterService,
) {

    @GetMapping("/{executionType}/{id}")
    fun getAuctionBet(@PathVariable executionType: BetExecutionType, @PathVariable id: Long): AuctionBetResponse =
        auctionBetFilterService.getAuctionBet(id, executionType)

    @PostMapping
    fun getAuctionWithFilters(
        @RequestBody request: AuctionBetPageableWithFilterRequest,
    ): PageResponse<AuctionBetResponse> = auctionBetFilterService.getWithFilters(request)

    @PostMapping("/personal")
    fun getPersonalAuctionWithFilters(
        @RequestBody request: AuctionPersonalBetPageableWithFilterRequest,
    ): PageResponse<PersonalAuctionBetResponse> = auctionBetFilterService.getWithFilters(request)

    @GetMapping("/{executionType}/{id}/leader")
    fun getLeaderJoin(
        @PathVariable executionType: BetExecutionType,
        @PathVariable id: Long,
        @RequestParam value: String,
    ): AuctionJoinResponse =
        auctionBetFilterService.getLeader(betId = id, target = value, executionType = executionType)

    @PostMapping("/joins")
    fun getCustomJoinsWithFilters(
        @RequestBody request: AuctionJoinsPageableWithFilterRequest,
    ): PageResponse<AuctionJoinResponse> = auctionBetFilterService.getAuctionJoinsWithFilters(request)
}