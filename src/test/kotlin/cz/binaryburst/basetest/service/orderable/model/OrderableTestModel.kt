package cz.binaryburst.basetest.service.orderable.model

import cz.binaryburst.generic.model.OrderableModel


data class OrderableTestModel(
    override var id: Long,
    var name: String,
    override var position: Int?,
) : OrderableModel<Long>()