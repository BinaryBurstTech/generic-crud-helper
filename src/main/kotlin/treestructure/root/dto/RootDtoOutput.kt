package cz.binaryburst.treestructure.root.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import cz.binaryburst.generic.dto.BaseDtoOutput
import cz.binaryburst.treestructure.zone.dto.ZoneDtoOutput

data class RootDtoOutput @JsonCreator constructor(
    @JsonProperty("id") override val id: Long = 0,
    @JsonProperty("name") var name: String = "",
    @JsonProperty("zones") var zones: List<ZoneDtoOutput> = emptyList()
) : BaseDtoOutput<Long>()