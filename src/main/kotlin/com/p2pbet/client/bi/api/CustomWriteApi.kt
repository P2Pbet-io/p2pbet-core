package com.p2pbet.client.bi.api

import com.p2pbet.client.bi.api.model.custom.CloseCustomBetDTO
import com.p2pbet.client.bi.api.model.execution.ExecutionResponseDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

interface CustomWriteApi {
    @PostMapping("/api/v1/custom/close")
    fun closeCustomBet(
        @RequestBody request: CloseCustomBetDTO
    ): ExecutionResponseDTO
}