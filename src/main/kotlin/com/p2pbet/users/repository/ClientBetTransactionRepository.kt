package com.p2pbet.users.repository

import com.p2pbet.users.entity.ClientBetTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ClientBetTransactionRepository : JpaRepository<ClientBetTransactionEntity, UUID>,
    JpaSpecificationExecutor<ClientBetTransactionEntity> {

}