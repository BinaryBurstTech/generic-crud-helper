package cz.binaryburst.treestructure.node.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import cz.binaryburst.generic.dto.OrderableDtoInput

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "nodeType"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = FolderDtoInput::class, name = "FOLDER"),
    JsonSubTypes.Type(value = FileDtoInput::class, name = "FILE")
)
abstract class NodeDtoInput @JsonCreator constructor(
    @JsonProperty("id") override var id: Long = 0,
    @JsonProperty("name") open var name: String = "",
    @JsonProperty("position") override var position: Int? = null,
    @JsonProperty("parentId") open var parentId: Long? = null,
    @JsonProperty("groupId") open var groupId: Long = 0,
    @JsonProperty("nodeType") open val nodeType: String
) : OrderableDtoInput<Long>()

