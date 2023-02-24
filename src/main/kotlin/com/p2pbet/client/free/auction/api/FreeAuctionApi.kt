package com.p2pbet.client.free.auction.api

import com.p2pbet.client.bi.api.model.auction.CloseAuctionBetDTO
import com.p2pbet.client.free.auction.api.model.CreateAuctionBetDTO
import com.p2pbet.client.free.auction.api.model.ExecutionFreeAuctionResponseDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

interface FreeAuctionApi {
    @PostMapping("/api/v1/free/auction/system/create")
    fun createAuctionBet(
        @RequestBody request: CreateAuctionBetDTO,
    ): ExecutionFreeAuctionResponseDTO

    @PostMapping("/api/v1/free/auction/system/close")
    fun closeAuctionBet(
        @RequestBody request: CloseAuctionBetDTO,
    ): ExecutionFreeAuctionResponseDTO
}