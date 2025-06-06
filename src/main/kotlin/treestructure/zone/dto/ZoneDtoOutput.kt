package cz.binaryburst.treestructure.zone.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import cz.binaryburst.generic.dto.BaseDtoOutput
import cz.binaryburst.treestructure.group.dto.GroupDtoOutput

data class ZoneDtoOutput @JsonCreator constructor(
    @JsonProperty("id") override val id: Long = 0,
    @JsonProperty("name") var name: String = "",
    @JsonProperty("rootId") var rootId: Long = 0,
    @JsonProperty("groups") var groups: List<GroupDtoOutput> = emptyList()
) : BaseDtoOutput<Long>()