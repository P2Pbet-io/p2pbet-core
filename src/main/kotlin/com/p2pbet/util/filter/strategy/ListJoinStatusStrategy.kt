package com.p2pbet.util.filter.strategy

import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.model.FilteredJoinStatusList.Companion.fromString
import com.p2pbet.util.filter.specification.GenericSpecification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

class ListJoinStatusStrategy : ParsingStrategy {
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
                        *(value!! as List<JoinStatus>).map {
                            builder.equal(GenericSpecification.extractPath<JoinStatus>(path, fieldName), it)
                        }.toTypedArray()
                    )
                )
            }

            else -> null
        }
    }

    override fun parse(value: String?, fieldClass: KClass<out Any>): List<JoinStatus>? = value?.fromString()?.list
}