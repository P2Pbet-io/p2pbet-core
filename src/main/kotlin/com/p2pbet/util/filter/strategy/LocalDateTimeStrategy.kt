package com.p2pbet.util.filter.strategy

import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.specification.GenericSpecification
import java.time.LocalDateTime
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

class LocalDateTimeStrategy : ParsingStrategy {
    override fun buildPredicate(
        builder: CriteriaBuilder,
        path: Path<*>,
        fieldName: String,
        condition: Condition?,
        value: Any?,
    ): Predicate? {
        return when (condition) {
            Condition.GREATER_THAN -> builder.greaterThan(
                GenericSpecification.extractPath(path, fieldName),
                value as LocalDateTime
            )

            Condition.LESS_THAN -> builder.lessThan(
                GenericSpecification.extractPath(path, fieldName),
                value as LocalDateTime
            )

            Condition.GREATER_EQUAL_THAN -> builder.greaterThanOrEqualTo(
                GenericSpecification.extractPath(
                    path,
                    fieldName
                ), value as LocalDateTime
            )

            Condition.LESS_EQUAL_THAN -> builder.lessThanOrEqualTo(
                GenericSpecification.extractPath(path, fieldName),
                value as LocalDateTime
            )

            else -> super.buildPredicate(builder, path, fieldName, condition, value)
        }
    }

    override fun parse(value: String?, fieldClass: KClass<out Any>): Any? {
        return LocalDateTime.parse(value)
    }
}