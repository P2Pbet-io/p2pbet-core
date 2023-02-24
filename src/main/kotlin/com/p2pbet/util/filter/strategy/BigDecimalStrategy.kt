package com.p2pbet.util.filter.strategy

import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.specification.GenericSpecification
import java.math.BigDecimal
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

open class BigDecimalStrategy : ParsingStrategy {

    override fun buildPredicate(
        builder: CriteriaBuilder,
        path: Path<*>,
        fieldName: String,
        condition: Condition?,
        value: Any?,
    ): Predicate? {
        return when (condition) {
            Condition.GREATER_EQUAL_THAN -> builder.greaterThanOrEqualTo(
                GenericSpecification.extractPath(
                    path,
                    fieldName
                ), value as BigDecimal
            )

            Condition.LESS_EQUAL_THAN -> builder.lessThanOrEqualTo(
                GenericSpecification.extractPath(path, fieldName),
                value as BigDecimal
            )

            Condition.LESS_THAN -> builder.lessThan(
                GenericSpecification.extractPath(path, fieldName),
                value as BigDecimal
            )

            Condition.GREATER_THAN -> builder.greaterThan(
                GenericSpecification.extractPath(path, fieldName),
                value as BigDecimal
            )

            else -> super.buildPredicate(builder, path, fieldName, condition, value)
        }
    }

    override fun parse(value: String?, fieldClass: KClass<out Any>): Any? {
        return value?.toBigDecimal()
    }
}