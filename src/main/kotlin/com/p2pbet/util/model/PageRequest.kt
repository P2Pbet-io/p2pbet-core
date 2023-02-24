package com.p2pbet.util.model

import com.p2pbet.util.model.Sort.Companion.toDomainSort


open class PageRequest(
    open val page: Int,
    open val size: Int,
    open val sort: Sort?
) {
    companion object {
        fun PageRequest.toDomainPageRequest() = if (sort == null) {
            org.springframework.data.domain.PageRequest.of(page, size)
        } else {
            org.springframework.data.domain.PageRequest.of(page, size, sort!!.toDomainSort())
        }
    }
}