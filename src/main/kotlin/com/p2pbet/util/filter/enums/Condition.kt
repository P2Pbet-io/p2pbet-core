package com.p2pbet.util.filter.enums

import io.swagger.annotations.ApiModel

@ApiModel(description = "Условие выборки")
enum class Condition {
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    GREATER_EQUAL_THAN,
    LESS_THAN,
    LESS_EQUAL_THAN,
    LIKE,
    NONE,
    IN
}