package cz.binaryburst.basetest.service.embeddable.model.partial

import cz.binaryburst.generic.model.BasePartialModel

data class EmbeddedChildTestModel(
    var name: String
): BasePartialModel()