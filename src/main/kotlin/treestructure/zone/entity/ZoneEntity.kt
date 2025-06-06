package cz.binaryburst.treestructure.zone.entity

import cz.binaryburst.generic.entity.BaseEntity
import cz.binaryburst.treestructure.group.entity.GroupEntity
import cz.binaryburst.treestructure.root.entity.RootEntity
import jakarta.persistence.*

@Entity
@Table(name = "zones")
class ZoneEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,

    @Column(nullable = false)
    var name: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_id", nullable = false)
    var root: RootEntity,

    @OneToMany(mappedBy = "zone", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val groups: MutableList<GroupEntity> = mutableListOf()
) : BaseEntity<Long>()