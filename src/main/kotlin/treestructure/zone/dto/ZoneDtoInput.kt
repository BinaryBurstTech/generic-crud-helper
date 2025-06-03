package cz.binaryburst.treestructure.zone.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import cz.binaryburst.generic.dto.BaseDtoInput

data class ZoneDtoInput @JsonCreator constructor(
    @JsonProperty("id") override var id: Long = 0,
    @JsonProperty("name") var name: String = "",
    @JsonProperty("rootId") var rootId: Long = 0
) : BaseDtoInput<Long>()

