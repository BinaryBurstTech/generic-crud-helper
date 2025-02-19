package cz.binaryburst.basetest.service.orderable.entity

import cz.binaryburst.generic.entity.OrderableEntity
import jakarta.persistence.*

@Entity
@Table(name = "_orderable_test")
class OrderableTestEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,

    @Column(name = "name")
    var name: String = "",

    @Column(name = "position")
    override var position: Int,
) : OrderableEntity<Long>()