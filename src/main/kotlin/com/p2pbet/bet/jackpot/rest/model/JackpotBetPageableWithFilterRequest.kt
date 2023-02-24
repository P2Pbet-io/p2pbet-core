package com.p2pbet.bet.jackpot.rest.model

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.util.filter.annotation.SearchField
import com.p2pbet.util.filter.custom.PeriodFilter
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.model.FilteredBaseEvent
import com.p2pbet.util.filter.model.FilteredBetStatusList
import com.p2pbet.util.model.PageRequest
import com.p2pbet.util.model.Sort
import java.math.BigInteger
import java.time.LocalDateTime

data class JackpotBetPageableWithFilterRequest(
    override val page: Int,
    override val size: Int,
    override val sort: Sort?,
    @SearchField(entityField = "id", operator = Condition.IN)
    val ids: List<Long>?,
    @SearchField(entityField = "baseEvent", operator = Condition.IN)
    val events: List<FilteredBaseEvent>?,
    @SearchField(entityField = "status", operator = Condition.IN)
    val statusList: FilteredBetStatusList?,
    @SearchField(entityField = "baseBet.createdBlockNumber", operator = Condition.LESS_EQUAL_THAN)
    val lastBlockNumber: BigInteger?,
    @SearchField(entityField = "baseBet.lockDate", operator = Condition.GREATER_THAN)
    val greaterLockTime: LocalDateTime?,
    val periodFilter: List<PeriodFilter>?,
    @SearchField(entityField = "baseBet.executionType", operator = Condition.EQUALS)
    val executionType: BetExecutionType,
) : PageRequest(page, size, sort)
