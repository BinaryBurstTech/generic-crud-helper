package cz.binaryburst.basetest.service.embeddable.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import cz.binaryburst.basetest.service.embeddable.dto.partial.EmbeddedChildTestOutputDto
import cz.binaryburst.generic.dto.BaseDtoOutput

data class EmbeddedTestDtoOutput @JsonCreator constructor(
    @JsonProperty("id") override var id: Long = 0,
    @JsonProperty("name") var name: String = "",
    @JsonProperty("embedded") var embeddedChildTestOutputDto: EmbeddedChildTestOutputDto

) : BaseDtoOutput<Long>()


