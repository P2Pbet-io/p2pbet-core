package com.p2pbet.p2pevent.controller.model

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.util.model.PageRequest

data class EventWithPopularityPageRequest(
    override val page: Int,
    override val size: Int,
    val executionType: BetExecutionType,
) : PageRequest(page, size, null)
