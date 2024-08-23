package cz.binaryburst.basetest.service.embeddable.entity

import cz.binaryburst.basetest.service.embeddable.entity.partial.EmbeddedChildTestEntity
import cz.binaryburst.generic.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "_embedded_test")
class EmbeddedTestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = Long.MIN_VALUE,

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Embedded
    var embeddedChildTestEntity: EmbeddedChildTestEntity,
) : BaseEntity<Long>()

