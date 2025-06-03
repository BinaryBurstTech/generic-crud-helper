package cz.binaryburst.treestructure.group.entity

import cz.binaryburst.generic.entity.OrderableEntity
import jakarta.persistence.*
import cz.binaryburst.treestructure.node.entity.NodeEntity
import cz.binaryburst.treestructure.zone.entity.ZoneEntity

@Entity
@Table(name = "groups")
class GroupEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,

    @Column(nullable = false)
    var name: String = "",

    @Column(name = "position", nullable = false)
    override var position: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    var zone: ZoneEntity,

    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val nodes: MutableList<NodeEntity> = mutableListOf()
) : OrderableEntity<Long>()