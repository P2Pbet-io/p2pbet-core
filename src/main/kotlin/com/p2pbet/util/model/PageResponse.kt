package com.p2pbet.util.model

import org.springframework.data.domain.Page

open class PageResponse<T>(
    open val totalElements: Long,
    open val totalPages: Int,
    open val result: List<T>,
) {
    companion object {
        fun <T> Page<T>.convertToPageResponse(): PageResponse<T> = PageResponse(
            totalElements = totalElements,
            totalPages = totalPages,
            result = content
        )

        fun <T, V> Page<T>.convertToPageResponseWithTransform(transform: (List<T>) -> List<V>): PageResponse<V> =
            PageResponse(
                totalElements = totalElements,
                totalPages = totalPages,
                result = transform(content)
            )
    }
}