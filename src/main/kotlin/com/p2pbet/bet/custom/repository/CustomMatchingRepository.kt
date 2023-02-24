package com.p2pbet.bet.custom.repository

import com.p2pbet.bet.custom.entity.CustomMatchingEntity
import com.p2pbet.bet.custom.entity.CustomP2PBetEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomMatchingRepository : JpaRepository<CustomMatchingEntity, Long> {
    fun findFirstByCustomBet(customP2PBetEntity: CustomP2PBetEntity): CustomMatchingEntity
}