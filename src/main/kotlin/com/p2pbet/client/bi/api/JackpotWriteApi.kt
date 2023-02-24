package com.p2pbet.client.bi.api

import com.p2pbet.client.bi.api.model.execution.ExecutionResponseDTO
import com.p2pbet.client.bi.api.model.jackpot.CloseJackpotBetDTO
import com.p2pbet.client.bi.api.model.jackpot.CreateJackpotBetDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

interface JackpotWriteApi {
    @PostMapping("/api/v1/jackpot/create")
    fun createJackpotBet(
        @RequestBody request: CreateJackpotBetDTO,
    ): ExecutionResponseDTO

    @PostMapping("/api/v1/jackpot/close")
    fun closeJackpotBet(
        @RequestBody request: CloseJackpotBetDTO,
    ): ExecutionResponseDTO
}