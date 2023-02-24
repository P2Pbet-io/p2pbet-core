package com.p2pbet.util.filter.specification

import com.p2pbet.util.filter.annotation.SearchField
import com.p2pbet.util.filter.enums.Condition
import com.p2pbet.util.filter.model.SearchCriteria
import org.springframework.data.jpa.domain.Specification
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

class SpecificationBuilder<T> {
    private val params: MutableList<SearchCriteria> = ArrayList()

    fun with(filterDTO: Any?, searchCriteriaList: List<SearchCriteria>): SpecificationBuilder<T> {
        addSearchCriteria(filterDTO)
        searchCriteriaList.map { params.add(it) }
        return this
    }

    fun build(): Specification<T>? {
        if (params.isEmpty()) {
            return null
        }
        val specs = ArrayList<Specification<T>>()
        for (param in params) {
            specs.add(GenericSpecification(param))
        }
        var result = specs[0]
        for (i in 1 until specs.size) {
            result = Specification.where(result).and(specs[i])
        }
        return result
    }

    private fun addSearchCriteria(instance: Any?) {
        if (instance?.javaClass?.isEnum == true || instance == null) return
        val clazz = instance.javaClass.kotlin
        clazz.declaredMemberProperties.forEach {
            it.findAnnotation<SearchField>()
                ?.let { c ->
                    it.get(instance)
                        ?.let { k ->
                            params.add(
                                SearchCriteria(
                                    key = c.entityField,
                                    operation = if (c.linkField.isNotEmpty()) Condition.valueOf(
                                        getProperty(
                                            instance,
                                            c.linkField
                                        )
                                    ) else c.operator,
                                    value = k,
                                    joinedEntity = c.joinedEntity.ifEmpty { null }
                                )
                            )
                        }
                }.runCatching { addSearchCriteria(it.get(instance)) }
        }
    }


    private fun getProperty(instance: Any, fieldName: String): String {
        val clazz = instance.javaClass.kotlin
        clazz.declaredMemberProperties.forEach {
            if (it.name == fieldName) {
                return it.get(instance).toString()
            }
        }
        throw RuntimeException("Exception")
    }
}