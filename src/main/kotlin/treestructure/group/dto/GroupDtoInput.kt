package cz.binaryburst.treestructure.group.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import cz.binaryburst.generic.dto.OrderableDtoInput

data class GroupDtoInput @JsonCreator constructor(
    @JsonProperty("id") override var id: Long = 0,
    @JsonProperty("name") var name: String = "",
    @JsonProperty("position") override var position: Int? = null,
    @JsonProperty("zoneId") var zoneId: Long = 0
) : OrderableDtoInput<Long>()

