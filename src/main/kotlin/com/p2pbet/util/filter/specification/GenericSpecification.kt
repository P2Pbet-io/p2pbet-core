package com.p2pbet.util.filter.specification

import com.p2pbet.util.filter.model.SearchCriteria
import com.p2pbet.util.filter.strategy.ParsingStrategy
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.*

class GenericSpecification<T>(
    private val criteria: SearchCriteria,
) : Specification<T> {

    override fun toPredicate(
        root: Root<T>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder,
    ): Predicate? = with(criteria) {
        val nestedRoot = getNestedRoot(root, joinedEntity)
        val fieldClass = extractPath<Any>(nestedRoot, key).javaType.kotlin
        val strategy = ParsingStrategy.getStrategy(fieldClass)
        val value = strategy.parse(value.toString(), fieldClass)
        return strategy.buildPredicate(criteriaBuilder, nestedRoot, key, operation, value)
    }


    companion object {
        inline fun <reified T> extractPath(path: Path<*>, key: String): Path<T> = key
            .split(".")
            .toList()
            .fold(path) { acc, nextKey ->
                acc.get<Any>(nextKey) as Path<Any>
            } as Path<T>

    }


    private fun getNestedRoot(root: Root<T>, joinedEntity: String?): Path<*> {
        return if (joinedEntity?.isEmpty() != true && joinedEntity != null) {
            root.get<Any>(joinedEntity)
        } else {
            root
        }
    }
}