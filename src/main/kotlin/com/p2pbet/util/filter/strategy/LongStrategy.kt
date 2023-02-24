package com.p2pbet.util.filter.strategy

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.specification.GenericSpecification.Companion.extractPath
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

class LongStrategy : ParsingStrategy {
    override fun buildPredicate(
        builder: CriteriaBuilder,
        path: Path<*>,
        fieldName: String,
        condition: Condition?,
        value: Any?,
    ): Predicate? {
        return when (condition) {
            Condition.GREATER_EQUAL_THAN -> builder.greaterThanOrEqualTo(extractPath(path, fieldName), value as Long)
            Condition.LESS_EQUAL_THAN -> builder.lessThanOrEqualTo(extractPath(path, fieldName), value as Long)
            Condition.LESS_THAN -> builder.lessThan(extractPath(path, fieldName), value as Long)
            Condition.GREATER_THAN -> builder.greaterThan(extractPath(path, fieldName), value as Long)
            Condition.IN -> builder.and(
                builder.or(
                    *(value!! as List<Long>).map {
                        builder.equal(extractPath<Long>(path, fieldName), it)
                    }.toTypedArray()
                )
            )

            else -> super.buildPredicate(builder, path, fieldName, condition, value)
        }
    }

    override fun parse(value: String?, fieldClass: KClass<out Any>): Any? {
        return value?.runCatching {
            toLong()
        }?.getOrNull()
            ?: value?.let {
                jacksonObjectMapper().readValue(it, object : TypeReference<List<Long>>() {})
            }
    }
}