package cz.binaryburst.treestructure.root.entity

import cz.binaryburst.generic.entity.BaseEntity
import cz.binaryburst.treestructure.zone.entity.ZoneEntity
import jakarta.persistence.*

@Entity
@Table(name = "roots")
class RootEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,

    @Column(nullable = false)
    var name: String = "",

    @OneToMany(mappedBy = "root", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val zones: MutableList<ZoneEntity> = mutableListOf()
) : BaseEntity<Long>()