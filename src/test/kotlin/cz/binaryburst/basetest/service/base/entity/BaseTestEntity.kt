package cz.binaryburst.basetest.service.base.entity

import cz.binaryburst.generic.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "_base_test")
class BaseTestEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,

    @Column(name = "name")
    var name: String = "",
) : BaseEntity<Long>()