package com.p2pbet.util.filter.strategy

import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.specification.GenericSpecification
import java.util.*
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

class UUIDStrategy() : ParsingStrategy {
    override fun buildPredicate(
        builder: CriteriaBuilder,
        path: Path<*>,
        fieldName: String,
        condition: Condition?,
        value: Any?,
    ): Predicate? {
        return when (condition) {
            Condition.IN -> builder.and(
                builder.or(
                    *(value!! as List<UUID>).map {
                        builder.equal(
                            GenericSpecification.extractPath<UUID>(path, fieldName), it
                        )
                    }.toTypedArray()
                )
            )

            else -> super.buildPredicate(builder, path, fieldName, condition, value)
        }
    }

    override fun parse(value: String?, fieldClass: KClass<out Any>): Any {
        return runCatching {
            UUID.fromString(value)
        }.getOrNull()
            ?: value!!.fromString()
    }

    companion object {
        fun String.fromString(): List<UUID> =
            this.substring(1, this.length - 1)
                .split(", ")
                .toList()
                .map(UUID::fromString)
    }
}