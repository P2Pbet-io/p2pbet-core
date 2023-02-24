package com.p2pbet.util.filter.strategy

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.BetStatus
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.bet.jackpot.entity.enums.JackpotJoinStatus
import com.p2pbet.messaging.model.queue.ContractType
import com.p2pbet.p2pevent.entity.BaseEvent
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.specification.GenericSpecification
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.*
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

interface ParsingStrategy {

    fun parse(value: String?, fieldClass: KClass<out Any>): Any? {
        return value
    }

    fun buildPredicate(
        builder: CriteriaBuilder,
        path: Path<*>,
        fieldName: String,
        condition: Condition?,
        value: Any?,
    ): Predicate? {
        return when (condition) {
            Condition.EQUALS -> builder.equal(GenericSpecification.extractPath<Any>(path, fieldName), value)
            Condition.NOT_EQUALS -> builder.notEqual(GenericSpecification.extractPath<Any>(path, fieldName), value)
            Condition.LIKE -> builder.like(
                (GenericSpecification.extractPath<Any>(path, fieldName).`as`(String::class.java)), "%$value%"
            )

            else -> null
        }
    }

    companion object {
        fun getStrategy(fieldClass: KClass<out Any>): ParsingStrategy {
            return when (fieldClass) {
                LocalDateTime::class -> LocalDateTimeStrategy()
                Boolean::class -> BooleanStrategy()
                BigDecimal::class -> BigDecimalStrategy()
                BigInteger::class -> BigIntegerStrategy()
                UUID::class -> UUIDStrategy()
                BetStatus::class -> ListBetStatusStrategy()
                JoinStatus::class -> ListJoinStatusStrategy()
                JackpotJoinStatus::class -> ListJackpotJoinStatusStrategy()
                BaseEvent::class -> BaseEventStrategy()
                Long::class -> LongStrategy()
                ContractType::class -> ContractTypeStrategy()
                BetExecutionType::class -> BetExecutionTypeStrategy()
                else -> StringStrategy()
            }
        }
    }
}