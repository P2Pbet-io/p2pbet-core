package com.p2pbet.client.bi

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.client.bi.api.ExecutionApi
import com.p2pbet.client.bi.api.model.execution.ExecutionResponseDTO
import org.springframework.stereotype.Component
import java.util.*

@Component
class ExecutionBlockchainIntegrationClient(
    bscExecutionApi: ExecutionApi,
    polygonExecutionApi: ExecutionApi,
    avalancheExecutionApi: ExecutionApi,
    tronExecutionApi: ExecutionApi,
) : AbstractBlockchainIntegrationClient<ExecutionApi>(
    bscWriteApi = bscExecutionApi,
    polygonWriteApi = polygonExecutionApi,
    avalancheWriteApi = avalancheExecutionApi,
    tronWriteApi = tronExecutionApi
) {
    fun checkExecutionStatus(
        id: UUID,
        executionType: BetExecutionType,
    ): ExecutionResponseDTO =
        getApi(executionType).checkExecutionStatus(
            id = id
        )
}