package com.p2pbet.client.bi.api

import com.p2pbet.client.bi.api.model.execution.ExecutionResponseDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

interface ExecutionApi {
    @GetMapping("/api/v1/execution/{id}")
    fun checkExecutionStatus(
        @PathVariable id: UUID
    ): ExecutionResponseDTO
}