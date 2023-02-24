package com.p2pbet.util.filter.strategy

import com.p2pbet.bet.common.entity.enums.BetStatus
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.model.FilteredBetStatusList.Companion.fromString
import com.p2pbet.util.filter.specification.GenericSpecification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

class ListBetStatusStrategy : ParsingStrategy {
    override fun buildPredicate(
        builder: CriteriaBuilder,
        path: Path<*>,
        fieldName: String,
        condition: Condition?,
        value: Any?,
    ): Predicate? {
        return when (condition) {
            Condition.IN -> {
                return builder.and(
                    builder.or(
                        *(value!! as List<BetStatus>).map {
                            builder.equal(GenericSpecification.extractPath<BetStatus>(path, fieldName), it)
                        }.toTypedArray()
                    )
                )
            }

            else -> null
        }
    }

    override fun parse(value: String?, fieldClass: KClass<out Any>): List<BetStatus>? = value?.fromString()?.list
}