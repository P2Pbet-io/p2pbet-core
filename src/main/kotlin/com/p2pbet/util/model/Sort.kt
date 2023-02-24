package com.p2pbet.util.model

data class Sort(
    val direction: Direction,
    val property: String
) {
    companion object {
        fun Sort.toDomainSort(): org.springframework.data.domain.Sort = org.springframework.data.domain.Sort.by(
            direction.domainOrder, property
        )
    }
}

enum class Direction(val domainOrder: org.springframework.data.domain.Sort.Direction) {
    ASC(org.springframework.data.domain.Sort.Direction.ASC), DESC(org.springframework.data.domain.Sort.Direction.DESC)
}

