package com.p2pbet.util.filter.model

import com.p2pbet.util.filter.enums.Condition
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(description = "Критерий поиска")
class SearchCriteria(

    @ApiModelProperty(value = "Ключ", required = true, allowEmptyValue = false)
    val key: String,

    @ApiModelProperty(value = "Условие выборки", required = true, allowEmptyValue = false)
    val operation: Condition,

    @ApiModelProperty(value = "Значение", required = true, allowEmptyValue = false)
    val value: Any,

    @ApiModelProperty(value = "Наименование связанной сущности", required = false, allowEmptyValue = false)
    val joinedEntity: String?,

    )