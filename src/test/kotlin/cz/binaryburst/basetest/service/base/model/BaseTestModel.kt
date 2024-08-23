package cz.binaryburst.basetest.service.base.model

import cz.binaryburst.generic.model.BaseModel


data class BaseTestModel(
    override var id: Long,
    var name: String,
) : BaseModel<Long>()