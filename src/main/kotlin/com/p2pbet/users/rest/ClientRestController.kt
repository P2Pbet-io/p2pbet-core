package com.p2pbet.users.rest

import com.p2pbet.users.rest.model.*
import com.p2pbet.users.service.ClientBetFilterService
import com.p2pbet.util.model.PageResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/public/api/v1/client")
class ClientRestController(
    val clientBetFilterService: ClientBetFilterService,
) {
    @GetMapping("/{address}/info")
    fun getSummaryInfo(@PathVariable address: String): List<AggregatedClientStatusJoinResponse> =
        clientBetFilterService.getSummaryInfo(address)

    @PostMapping("/transactions")
    fun getClientTransactions(@RequestBody request: ClientTransactionPageableRequest): PageResponse<ClientTransactionResponse> =
        clientBetFilterService.getTransactions(request)

    @PostMapping("/joins")
    fun getClientJoins(@RequestBody request: ClientJoinPageableRequest): PageResponse<ClientJoinResponse> =
        clientBetFilterService.getJoins(request)
}