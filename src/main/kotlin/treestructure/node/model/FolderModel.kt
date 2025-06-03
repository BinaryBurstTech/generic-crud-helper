package cz.binaryburst.treestructure.node.model

import cz.binaryburst.treestructure.node.enums.NodeType

data class FolderModel(
    override val id: Long,
    override var name: String,
    override var position: Int?,
    override var parentId: Long?,
    override var groupId: Long,
    var children: List<NodeModel> = emptyList()
) : NodeModel(id, name, position, parentId, groupId, NodeType.FOLDER)