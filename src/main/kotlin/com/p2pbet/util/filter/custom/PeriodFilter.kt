package com.p2pbet.util.filter.custom

import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.specification.GenericSpecification
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl
import org.hibernate.query.criteria.internal.expression.BinaryArithmeticOperation
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path

class PeriodFilter(
    val period: Long,
    val condition: Condition,
) {
    companion object {
        fun <T> List<PeriodFilter>.toCriteriaRequest(leftKey: String, rightKey: String): Specification<T> =
            map {
                it.toCriteriaRequest<T>(
                    leftKey, rightKey
                )
            }.fold<Specification<T>, Specification<T>?>(null) { acc, specification ->
                acc?.and(specification) ?: specification
            }!!

        fun <T> PeriodFilter.toCriteriaRequest(leftKey: String, rightKey: String): Specification<T> =
            Specification { root, query, criteriaBuilder ->
                with(
                    criteriaBuilder.formDiffExpression(
                        leftFieldPath = GenericSpecification.extractPath(root, leftKey),
                        rightFieldPath = GenericSpecification.extractPath(root, rightKey)
                    )
                ) {
                    when (condition) {
                        Condition.EQUALS -> criteriaBuilder.equal(
                            this,
                            period
                        )

                        Condition.LESS_THAN -> criteriaBuilder.lessThan(
                            this,
                            period
                        )

                        Condition.LESS_EQUAL_THAN -> criteriaBuilder.lessThanOrEqualTo(
                            this,
                            period
                        )

                        Condition.GREATER_THAN -> criteriaBuilder.greaterThan(
                            this,
                            period
                        )

                        Condition.GREATER_EQUAL_THAN -> criteriaBuilder.greaterThanOrEqualTo(
                            this,
                            period
                        )

                        else -> throw RuntimeException("Unsupported condition")
                    }
                }
            }

        private fun CriteriaBuilder.formDiffExpression(
            leftFieldPath: Path<Any>,
            rightFieldPath: Path<Any>,
        ): Expression<Long> {
            return BinaryArithmeticOperation(
                this as CriteriaBuilderImpl,
                Long::class.java,
                BinaryArithmeticOperation.Operation.SUBTRACT,
                function("date_part", Long::class.java, literal("EPOCH"), leftFieldPath),
                function("date_part", Long::class.java, literal("EPOCH"), rightFieldPath)
            )
        }
    }
}