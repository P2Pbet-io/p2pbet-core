package com.p2pbet.util.filter.annotation

import com.p2pbet.util.filter.enums.Condition


@Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class SearchField(
    val entityField: String,
    val operator: Condition = Condition.NONE,
    val linkField: String = "",
    val joinedEntity: String = "",
)
