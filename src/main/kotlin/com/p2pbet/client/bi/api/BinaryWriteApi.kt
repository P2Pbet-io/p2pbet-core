package com.p2pbet.client.bi.api

import com.p2pbet.client.bi.api.model.binary.CloseBinaryBetDTO
import com.p2pbet.client.bi.api.model.binary.CreateBinaryBetDTO
import com.p2pbet.client.bi.api.model.execution.ExecutionResponseDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

interface BinaryWriteApi {
    @PostMapping("/api/v1/binary/create")
    fun createBinaryBet(
        @RequestBody request: CreateBinaryBetDTO
    ): ExecutionResponseDTO

    @PostMapping("/api/v1/binary/close")
    fun closeBinaryBet(
        @RequestBody request: CloseBinaryBetDTO
    ): ExecutionResponseDTO
}