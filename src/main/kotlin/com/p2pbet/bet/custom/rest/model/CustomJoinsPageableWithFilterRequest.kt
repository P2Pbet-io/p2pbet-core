package com.p2pbet.bet.custom.rest.model

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.util.filter.annotation.SearchField
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.model.FilteredJoinStatusList
import com.p2pbet.util.model.PageRequest
import com.p2pbet.util.model.Sort
import java.util.*

data class CustomJoinsPageableWithFilterRequest(
    override val page: Int,
    override val size: Int,
    override val sort: Sort?,
    @SearchField(entityField = "customBet.betId", operator = Condition.EQUALS)
    val customBet: Long?,
    @SearchField(entityField = "client", operator = Condition.EQUALS)
    val client: String?,
    @SearchField(entityField = "status", operator = Condition.IN)
    val statusList: FilteredJoinStatusList?,
    @SearchField(entityField = "id", operator = Condition.IN)
    val ids: List<UUID>?,
    @SearchField(entityField = "executionType", operator = Condition.EQUALS)
    val executionType: BetExecutionType,
) : PageRequest(page, size, sort)
