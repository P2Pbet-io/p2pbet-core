package com.p2pbet.util.filter.strategy

import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.specification.GenericSpecification
import com.p2pbet.util.model.Direction
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

class DirectionStrategy() : ParsingStrategy {

    override fun buildPredicate(
        builder: CriteriaBuilder,
        path: Path<*>,
        fieldName: String,
        condition: Condition?,
        value: Any?,
    ): Predicate? {
        return when (condition) {
            Condition.EQUALS -> return builder.equal(
                GenericSpecification.extractPath<Direction>(path, fieldName),
                value
            )

            else -> null
        }
    }

    override fun parse(value: String?, fieldClass: KClass<out Any>): Any? {
        return java.lang.Enum.valueOf(Direction::class.java, value)
    }
}