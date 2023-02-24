package com.p2pbet.users.rest.model

import com.p2pbet.util.filter.annotation.SearchField
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.model.PageRequest
import com.p2pbet.util.model.Sort
import java.time.LocalDateTime

data class ClientTransactionPageableRequest(
    override val page: Int,
    override val size: Int,
    override val sort: Sort?,
    @SearchField(entityField = "clientBetJoinEntity.clientAddress", operator = Condition.EQUALS)
    val clientAddress: String?,
    @SearchField(entityField = "createdDate", operator = Condition.LESS_EQUAL_THAN)
    val createdDate: LocalDateTime?,
) : PageRequest(page, size, sort)
