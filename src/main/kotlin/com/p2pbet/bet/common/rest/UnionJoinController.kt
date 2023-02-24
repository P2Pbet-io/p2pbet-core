package com.p2pbet.bet.common.rest

import com.p2pbet.bet.common.rest.model.UnionJoinRequest
import com.p2pbet.bet.common.rest.model.UnionJoinResponse
import com.p2pbet.bet.common.service.UnionJoinFilterService
import com.p2pbet.util.model.PageResponse
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public/api/v1/common/joins")
class UnionJoinController(
    val unionJoinFilterService: UnionJoinFilterService,
) {

    //@PostMapping
    @Deprecated(
        message = "New client domain. JackpotBetRestController.getJackpotJoinsWithFilters",
        replaceWith = ReplaceWith("JackpotBetRestController.getJackpotJoinsWithFilters(request)")
    )
    fun getUnionJoin(
        @RequestBody request: UnionJoinRequest,
    ): PageResponse<UnionJoinResponse> = unionJoinFilterService.getUnionJoinWithFilters(request)
}