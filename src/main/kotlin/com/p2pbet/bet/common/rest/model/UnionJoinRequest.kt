package com.p2pbet.bet.common.rest.model

import com.p2pbet.util.model.Direction
import com.p2pbet.util.model.PageRequest
import com.p2pbet.util.model.Sort

data class UnionJoinRequest(
    override val page: Int,
    override val size: Int,
    override val sort: Sort = Sort(
        direction = Direction.DESC,
        property = "createdDate"
    ),
) : PageRequest(page, size, sort)
