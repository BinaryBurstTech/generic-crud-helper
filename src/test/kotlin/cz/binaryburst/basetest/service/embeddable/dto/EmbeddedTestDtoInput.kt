package cz.binaryburst.basetest.service.embeddable.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import cz.binaryburst.basetest.service.embeddable.dto.partial.EmbeddedChildTestInsertDto
import cz.binaryburst.generic.dto.BaseDtoInput

data class EmbeddedTestDtoInput @JsonCreator constructor(
    @JsonProperty("id") override var id: Long = Long.MIN_VALUE,
    @JsonProperty("name") var name: String = "",
    @JsonProperty("embedded") var embeddedChildTestInsertDto: EmbeddedChildTestInsertDto = EmbeddedChildTestInsertDto()
) : BaseDtoInput<Long>()


