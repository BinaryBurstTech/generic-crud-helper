package cz.binaryburst.basetest.service.embeddable.entity.partial

import cz.binaryburst.generic.entity.BasePartialEntity
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class EmbeddedChildTestEntity(
    @Column(name = "child_value", nullable = false)
    var value: String = ""
): BasePartialEntity()