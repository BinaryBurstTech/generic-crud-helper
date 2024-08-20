package cz.binaryburst.basetest.service.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import cz.binaryburst.generic.dto.BaseOutputDto

data class BaseTestOutputDto @JsonCreator constructor(
    @JsonProperty("id") override var id: Long = Long.MIN_VALUE,
    @JsonProperty("name") var name: String = "",
) : BaseOutputDto<Long>()