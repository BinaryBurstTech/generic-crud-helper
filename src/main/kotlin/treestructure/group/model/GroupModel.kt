package cz.binaryburst.treestructure.group.model

import cz.binaryburst.generic.model.OrderableModel
import cz.binaryburst.treestructure.node.model.NodeModel

data class GroupModel(
    override val id: Long,
    var name: String,
    override var position: Int?,
    var zoneId: Long,
    var nodes: List<NodeModel> = emptyList()
) : OrderableModel<Long>()