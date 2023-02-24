package com.p2pbet.client.bi.api

import com.p2pbet.client.bi.api.model.auction.CloseAuctionBetDTO
import com.p2pbet.client.bi.api.model.auction.CreateAuctionBetDTO
import com.p2pbet.client.bi.api.model.execution.ExecutionResponseDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

interface AuctionWriteApi {
    @PostMapping("/api/v1/auction/create")
    fun createAuctionBet(
        @RequestBody request: CreateAuctionBetDTO
    ): ExecutionResponseDTO

    @PostMapping("/api/v1/auction/close")
    fun closeAuctionBet(
        @RequestBody request: CloseAuctionBetDTO
    ): ExecutionResponseDTO
}