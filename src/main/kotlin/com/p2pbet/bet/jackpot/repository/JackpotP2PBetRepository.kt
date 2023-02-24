package com.p2pbet.bet.jackpot.repository

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.jackpot.entity.JackpotP2PBetEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface JackpotP2PBetRepository : JpaRepository<JackpotP2PBetEntity, UUID>,
    JpaSpecificationExecutor<JackpotP2PBetEntity> {
    fun findFirstByBetIdAndBaseBetExecutionType(
        betId: Long,
        executionType: BetExecutionType,
    ): JackpotP2PBetEntity

}