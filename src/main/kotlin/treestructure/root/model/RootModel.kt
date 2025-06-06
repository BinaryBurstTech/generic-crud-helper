package cz.binaryburst.treestructure.root.model

import cz.binaryburst.generic.model.BaseModel
import cz.binaryburst.treestructure.zone.model.ZoneModel

data class RootModel(
    override val id: Long,
    var name: String,
    var zones: List<ZoneModel> = emptyList()
) : BaseModel<Long>()