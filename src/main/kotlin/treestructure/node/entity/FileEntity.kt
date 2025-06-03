package cz.binaryburst.treestructure.node.entity

import cz.binaryburst.treestructure.group.entity.GroupEntity
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("FILE")
class FileEntity(
    name: String = "",
    position: Int = 0,
    parent: FolderEntity? = null,
    group: GroupEntity
) : NodeEntity(name = name, position = position, parent = parent, group = group)