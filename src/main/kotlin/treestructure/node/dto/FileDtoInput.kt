package cz.binaryburst.treestructure.node.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FileDtoInput @JsonCreator constructor(
    @JsonProperty("id") override var id: Long = 0,
    @JsonProperty("name") override var name: String = "",
    @JsonProperty("position") override var position: Int? = null,
    @JsonProperty("parentId") override var parentId: Long? = null,
    @JsonProperty("groupId") override var groupId: Long = 0
) : NodeDtoInput(id, name, position, parentId, groupId, "FILE")