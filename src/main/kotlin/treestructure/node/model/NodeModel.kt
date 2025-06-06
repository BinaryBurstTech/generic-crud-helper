package cz.binaryburst.treestructure.node.model

import cz.binaryburst.generic.model.OrderableModel
import cz.binaryburst.treestructure.node.enums.NodeType

abstract class NodeModel(
    override val id: Long,
    open var name: String,
    override var position: Int?,
    open var parentId: Long?,
    open var groupId: Long,
    open val nodeType: NodeType
) : OrderableModel<Long>()