package cz.binaryburst.treestructure.node.entity

import cz.binaryburst.treestructure.group.entity.GroupEntity
import jakarta.persistence.*

@Entity
@DiscriminatorValue("FOLDER")
class FolderEntity(
    name: String = "",
    position: Int = 0,
    parent: FolderEntity? = null,
    group: GroupEntity
) : NodeEntity(name = name, position = position, parent = parent, group = group) {

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val children: MutableList<NodeEntity> = mutableListOf()
}