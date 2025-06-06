package cz.binaryburst.treestructure.group.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import cz.binaryburst.generic.dto.OrderableDtoOutput
import cz.binaryburst.treestructure.node.dto.NodeDtoOutput

data class GroupDtoOutput @JsonCreator constructor(
    @JsonProperty("id") override val id: Long = 0,
    @JsonProperty("name") var name: String = "",
    @JsonProperty("position") override var position: Int = 0,
    @JsonProperty("zoneId") var zoneId: Long = 0,
    @JsonProperty("nodes") var nodes: List<NodeDtoOutput> = emptyList()
) : OrderableDtoOutput<Long>()