package com.p2pbet.util.filter.strategy

import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.specification.GenericSpecification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

class ContractTypeStrategy() : ParsingStrategy {

    override fun buildPredicate(
        builder: CriteriaBuilder,
        path: Path<*>,
        fieldName: String,
        condition: Condition?,
        value: Any?,
    ): Predicate? {
        return when (condition) {
            Condition.EQUALS -> return builder.equal(
                GenericSpecification.extractPath<ContractType>(path, fieldName),
                value
            )

            else -> null
        }
    }

    override fun parse(value: String?, fieldClass: KClass<out Any>): Any? {
        return java.lang.Enum.valueOf(ContractType::class.java, value)
    }
}