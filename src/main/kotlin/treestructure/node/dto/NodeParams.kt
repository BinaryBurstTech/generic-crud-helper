package cz.binaryburst.treestructure.node.dto

import cz.binaryburst.generic.service.OrderableParams

data class NodeParams(
    val groupId: Long? = null,
    val parentId: Long? = null
) : OrderableParams