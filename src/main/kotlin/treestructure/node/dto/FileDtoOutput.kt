package cz.binaryburst.treestructure.node.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FileDtoOutput @JsonCreator constructor(
    @JsonProperty("id") override val id: Long = 0,
    @JsonProperty("name") override var name: String = "",
    @JsonProperty("position") override var position: Int = 0,
    @JsonProperty("parentId") override var parentId: Long? = null,
    @JsonProperty("groupId") override var groupId: Long = 0
) : NodeDtoOutput(id, name, position, parentId, groupId, "FILE")