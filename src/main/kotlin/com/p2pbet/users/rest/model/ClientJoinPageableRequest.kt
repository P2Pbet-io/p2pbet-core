package com.p2pbet.users.rest.model

import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.util.filter.annotation.SearchField
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.model.PageRequest
import com.p2pbet.util.model.Sort

data class ClientJoinPageableRequest(
    override val page: Int,
    override val size: Int,
    override val sort: Sort?,
    @SearchField(entityField = "clientAddress", operator = Condition.EQUALS)
    val clientAddress: String?,
    @SearchField(entityField = "betType", operator = Condition.EQUALS)
    val betType: ContractType?,
) : PageRequest(page, size, sort)
