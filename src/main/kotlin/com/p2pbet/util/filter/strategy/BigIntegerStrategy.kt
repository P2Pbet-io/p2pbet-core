package com.p2pbet.util.filter.strategy

import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.specification.GenericSpecification.Companion.extractPath
import java.math.BigInteger
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

class BigIntegerStrategy : ParsingStrategy {
    override fun buildPredicate(
        builder: CriteriaBuilder,
        path: Path<*>,
        fieldName: String,
        condition: Condition?,
        value: Any?,
    ): Predicate? {
        return when (condition) {
            Condition.GREATER_EQUAL_THAN -> builder.greaterThanOrEqualTo(
                extractPath(path, fieldName),
                value as BigInteger
            )

            Condition.LESS_EQUAL_THAN -> builder.lessThanOrEqualTo(extractPath(path, fieldName), value as BigInteger)
            Condition.LESS_THAN -> builder.lessThan(extractPath(path, fieldName), value as BigInteger)
            Condition.GREATER_THAN -> builder.greaterThan(extractPath(path, fieldName), value as BigInteger)
            else -> super.buildPredicate(builder, path, fieldName, condition, value)
        }
    }

    override fun parse(value: String?, fieldClass: KClass<out Any>): Any? {
        return value?.toBigInteger()
    }
}