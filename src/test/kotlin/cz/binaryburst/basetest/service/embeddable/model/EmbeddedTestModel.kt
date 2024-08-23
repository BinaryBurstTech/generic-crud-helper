package cz.binaryburst.basetest.service.embeddable.model

import cz.binaryburst.basetest.service.embeddable.model.partial.EmbeddedChildTestModel
import cz.binaryburst.generic.model.BaseModel


data class EmbeddedTestModel(
    override var id: Long,
    var name: String,
    var embeddedChildTestModel: EmbeddedChildTestModel
) : BaseModel<Long>()

