package cz.binaryburst.basetest.service.base.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import cz.binaryburst.generic.dto.BaseDtoOutput

data class BaseTestDtoOutput @JsonCreator constructor(
    @JsonProperty("id") override var id: Long = Long.MIN_VALUE,
    @JsonProperty("name") var name: String = "",
) : BaseDtoOutput<Long>()