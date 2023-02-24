package com.p2pbet.bet.custom.service

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.messaging.model.log.custom.CustomBetCreatedLog
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomCreationBetService(
    val customBetService: CustomBetService,
    val customMatchingService: CustomMatchingService,
) {
    @Transactional
    fun onCreate(createLog: CustomBetCreatedLog, executionType: BetExecutionType) = createLog
        .let { customBetService.onCreate(createLog, executionType) } // create custom bet
        .let(customMatchingService::onCreate) // create matching info
}