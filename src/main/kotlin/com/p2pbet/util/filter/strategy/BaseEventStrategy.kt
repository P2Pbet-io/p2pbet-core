package com.p2pbet.util.filter.strategy

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.p2pbet.p2pevent.controller.model.EventType
import com.p2pbet.p2pevent.entity.BaseEvent
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.model.FilteredBaseEvent
import com.p2pbet.util.filter.specification.GenericSpecification
import java.util.*
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

class BaseEventStrategy : ParsingStrategy {
    override fun buildPredicate(
        builder: CriteriaBuilder,
        path: Path<*>,
        fieldName: String,
        condition: Condition?,
        value: Any?,
    ): Predicate? {
        return when (condition) {
            Condition.EQUALS -> {
                return builder.equal(
                    GenericSpecification.extractPath<BaseEvent>(path, fieldName),
                    value as BaseEvent
                )
            }

            Condition.IN -> {
                return builder.and(
                    builder.or(
                        *(value as List<BaseEvent>).map {
                            builder.equal(GenericSpecification.extractPath<BaseEvent>(path, fieldName), it)
                        }.toTypedArray()
                    )
                )
            }

            else -> null
        }
    }

    override fun parse(value: String?, fieldClass: KClass<out Any>): Any? = value?.let { stringValue ->
        stringValue.runCatching {
            BaseEvent(eventFromString(), EventType.crypto, "", "", "", "")
        }.getOrNull()
            ?: stringValue.eventListFromString()
                .map { BaseEvent(it, EventType.crypto, "", "", "", "") }
    }

    companion object {
        fun String.eventFromString(): UUID =
            jacksonObjectMapper().readValue(this, FilteredBaseEvent::class.java).eventId

        fun String.eventListFromString(): List<UUID> =
            jacksonObjectMapper().readValue(this, object : TypeReference<List<FilteredBaseEvent>>() {})
                .map { it.eventId }
    }
}