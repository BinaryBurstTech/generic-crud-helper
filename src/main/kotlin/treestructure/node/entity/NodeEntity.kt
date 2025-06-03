package cz.binaryburst.treestructure.node.entity

import cz.binaryburst.generic.entity.OrderableEntity
import cz.binaryburst.treestructure.group.entity.GroupEntity
import jakarta.persistence.*

@Entity
@Table(name = "nodes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "node_type", discriminatorType = DiscriminatorType.STRING)
abstract class NodeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long = 0,

    @Column(nullable = false)
    open var name: String = "",

    @Column(name = "position", nullable = false)
    override var position: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    open var parent: FolderEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    open var group: GroupEntity
) : OrderableEntity<Long>()