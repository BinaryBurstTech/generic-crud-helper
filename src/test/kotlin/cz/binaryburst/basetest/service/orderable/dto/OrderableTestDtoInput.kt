package cz.binaryburst.basetest.service.orderable.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import cz.binaryburst.generic.dto.OrderableDtoInput

data class OrderableTestDtoInput @JsonCreator constructor(
    @JsonProperty("id") override var id: Long = 0,
    @JsonProperty("name") var name: String = "",
    @JsonProperty("position") override var position: Int?,
) : OrderableDtoInput<Long>()