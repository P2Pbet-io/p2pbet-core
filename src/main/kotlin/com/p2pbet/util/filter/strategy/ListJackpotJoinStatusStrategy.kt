package com.p2pbet.util.filter.strategy

import com.p2pbet.bet.jackpot.entity.enums.JackpotJoinStatus
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.model.FilteredJackpotJoinStatusList.Companion.fromString
import com.p2pbet.util.filter.specification.GenericSpecification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

class ListJackpotJoinStatusStrategy : ParsingStrategy {
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
                        *(value!! as List<JackpotJoinStatus>).map {
                            builder.equal(GenericSpecification.extractPath<JackpotJoinStatus>(path, fieldName), it)
                        }.toTypedArray()
                    )
                )
            }

            else -> null
        }
    }

    override fun parse(value: String?, fieldClass: KClass<out Any>): List<JackpotJoinStatus>? =
        value?.fromString()?.list
}