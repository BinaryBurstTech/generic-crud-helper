package cz.binaryburst.treestructure.node.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import cz.binaryburst.generic.dto.OrderableDtoOutput

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "nodeType"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = FolderDtoOutput::class, name = "FOLDER"),
    JsonSubTypes.Type(value = FileDtoOutput::class, name = "FILE")
)
abstract class NodeDtoOutput @JsonCreator constructor(
    @JsonProperty("id") override val id: Long = 0,
    @JsonProperty("name") open var name: String = "",
    @JsonProperty("position") override var position: Int = 0,
    @JsonProperty("parentId") open var parentId: Long? = null,
    @JsonProperty("groupId") open var groupId: Long = 0,
    @JsonProperty("nodeType") open val nodeType: String
) : OrderableDtoOutput<Long>()