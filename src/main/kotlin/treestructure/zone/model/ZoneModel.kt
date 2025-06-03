package cz.binaryburst.treestructure.zone.model

import cz.binaryburst.generic.model.BaseModel
import cz.binaryburst.treestructure.group.model.GroupModel

data class ZoneModel(
    override val id: Long,
    var name: String,
    var rootId: Long,
    var groups: List<GroupModel> = emptyList()
) : BaseModel<Long>()