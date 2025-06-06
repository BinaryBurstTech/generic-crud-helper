package cz.binaryburst.treestructure.node.mapper

import cz.binaryburst.generic.mapper.IOrderableMapper
import cz.binaryburst.treestructure.group.repository.GroupRepository
import cz.binaryburst.treestructure.node.dto.*
import cz.binaryburst.treestructure.node.entity.FileEntity
import cz.binaryburst.treestructure.node.entity.FolderEntity
import cz.binaryburst.treestructure.node.entity.NodeEntity
import cz.binaryburst.treestructure.node.model.FileModel
import cz.binaryburst.treestructure.node.model.FolderModel
import cz.binaryburst.treestructure.node.model.NodeModel
import cz.binaryburst.treestructure.node.repository.NodeRepository
import org.springframework.stereotype.Component

@Component
class NodeMapper(
    private val groupRepository: GroupRepository,
    private val nodeRepository: NodeRepository
) : IOrderableMapper<NodeDtoInput, NodeDtoOutput, NodeModel, NodeEntity, Long> {

    override fun convertDtoToModel(dto: NodeDtoInput): NodeModel {
        return when (dto) {
            is FolderDtoInput -> FolderModel(
                id = dto.id,
                name = dto.name,
                position = dto.position,
                parentId = dto.parentId,
                groupId = dto.groupId
            )

            is FileDtoInput -> FileModel(
                id = dto.id,
                name = dto.name,
                position = dto.position,
                parentId = dto.parentId,
                groupId = dto.groupId
            )

            else -> throw IllegalArgumentException("Unknown NodeDtoInput type: ${dto::class.simpleName}")
        }
    }

    override fun convertModelToDtoOut(model: NodeModel): NodeDtoOutput {
        return when (model) {
            is FolderModel -> FolderDtoOutput(
                id = model.id,
                name = model.name,
                position = model.position ?: throw IllegalStateException("Node cannot be without position"),
                parentId = model.parentId,
                groupId = model.groupId,
                children = model.children.map { convertModelToDtoOut(it) }
            )

            is FileModel -> FileDtoOutput(
                id = model.id,
                name = model.name,
                position = model.position ?: throw IllegalStateException("Node cannot be without position"),
                parentId = model.parentId,
                groupId = model.groupId
            )

            else -> throw IllegalArgumentException("Unknown NodeModel type: ${model::class.simpleName}")
        }
    }

    override fun convertModelToEntity(model: NodeModel): NodeEntity {
        val group = groupRepository.findById(model.groupId).orElseThrow {
            IllegalArgumentException("Group with id ${model.groupId} not found")
        }
        val parent = model.parentId?.let { parentId ->
            nodeRepository.findById(parentId).orElseThrow {
                IllegalArgumentException("Parent node with id $parentId not found")
            } as? FolderEntity ?: throw IllegalArgumentException("Parent must be a folder")
        }

        return when (model) {
            is FolderModel -> FolderEntity(
                name = model.name,
                position = model.position ?: throw IllegalStateException("Node cannot be without position"),
                parent = parent,
                group = group
            ).apply {
                if (model.id != 0L) {
                    this.id = model.id
                }
            }

            is FileModel -> FileEntity(
                name = model.name,
                position = model.position ?: throw IllegalStateException("Node cannot be without position"),
                parent = parent,
                group = group
            ).apply {
                if (model.id != 0L) {
                    this.id = model.id
                }
            }

            else -> throw IllegalArgumentException("Unknown NodeModel type: ${model::class.simpleName}")
        }
    }

    override fun convertEntityToModel(entity: NodeEntity): NodeModel {
        return when (entity) {
            is FolderEntity -> FolderModel(
                id = entity.id,
                name = entity.name,
                position = entity.position,
                parentId = entity.parent?.id,
                groupId = entity.group.id,
                // Avoid loading children to prevent circular dependencies
                children = emptyList()
            )

            is FileEntity -> FileModel(
                id = entity.id,
                name = entity.name,
                position = entity.position,
                parentId = entity.parent?.id,
                groupId = entity.group.id
            )

            else -> throw IllegalArgumentException("Unknown NodeEntity type: ${entity::class.simpleName}")
        }
    }

    override fun updateEntityFromModel(entity: NodeEntity, model: NodeModel): NodeEntity {
        entity.name = model.name
        // Position is managed by OrderableService, don't update it here

        // Update parent if needed
        if (entity.parent?.id != model.parentId) {
            val parent = model.parentId?.let { parentId ->
                nodeRepository.findById(parentId).orElseThrow {
                    IllegalArgumentException("Parent node with id $parentId not found")
                } as? FolderEntity ?: throw IllegalArgumentException("Parent must be a folder")
            }
            entity.parent = parent
        }

        // Update group if needed
        if (entity.group.id != model.groupId) {
            val group = groupRepository.findById(model.groupId).orElseThrow {
                IllegalArgumentException("Group with id ${model.groupId} not found")
            }
            entity.group = group
        }

        return entity
    }

    override fun extractIdFromModel(model: NodeModel): Long? {
        return model.id
    }

    override fun convertEntityToDto(entity: NodeEntity): NodeDtoOutput {
        return when (entity) {
            is FolderEntity -> FolderDtoOutput(
                id = entity.id,
                name = entity.name,
                position = entity.position,
                parentId = entity.parent?.id,
                groupId = entity.group.id,
                // Avoid loading children to prevent circular dependencies
                children = emptyList()
            )

            is FileEntity -> FileDtoOutput(
                id = entity.id,
                name = entity.name,
                position = entity.position,
                parentId = entity.parent?.id,
                groupId = entity.group.id
            )

            else -> throw IllegalArgumentException("Unknown NodeEntity type: ${entity::class.simpleName}")
        }
    }
}