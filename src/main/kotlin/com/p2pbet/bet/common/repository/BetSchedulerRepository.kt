package com.p2pbet.bet.common.repository

import com.p2pbet.bet.common.entity.BetSchedulerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BetSchedulerRepository : JpaRepository<BetSchedulerEntity, UUID> {
}