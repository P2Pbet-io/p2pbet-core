package com.p2pbet.util.filter.strategy

import com.p2pbet.util.filter.enums.Condition
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

class BooleanStrategy() : ParsingStrategy {
    override fun buildPredicate(
        builder: CriteriaBuilder,
        path: Path<*>,
        fieldName: String,
        condition: Condition?,
        value: Any?,
    ): Predicate? {
        return super.buildPredicate(builder, path, fieldName, condition, (value as String).toBoolean())
    }
}