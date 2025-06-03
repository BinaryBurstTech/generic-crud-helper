package cz.binaryburst.treestructure.group.mapper

import cz.binaryburst.generic.mapper.IOrderableMapper
import cz.binaryburst.treestructure.group.dto.GroupDtoInput
import cz.binaryburst.treestructure.group.dto.GroupDtoOutput
import cz.binaryburst.treestructure.group.entity.GroupEntity
import cz.binaryburst.treestructure.group.model.GroupModel
import cz.binaryburst.treestructure.node.mapper.NodeMapper
import cz.binaryburst.treestructure.zone.repository.ZoneRepository
import org.springframework.stereotype.Component

@Component
class GroupMapper(
    private val nodeMapper: NodeMapper,
    private val zoneRepository: ZoneRepository
) : IOrderableMapper<GroupDtoInput, GroupDtoOutput, GroupModel, GroupEntity, Long> {

    override fun convertDtoToModel(dto: GroupDtoInput): GroupModel {
        return GroupModel(
            id = dto.id,
            name = dto.name,
            position = dto.position,
            zoneId = dto.zoneId
        )
    }

    override fun convertModelToDtoOut(model: GroupModel): GroupDtoOutput {
        return GroupDtoOutput(
            id = model.id,
            name = model.name,
            position = model.position ?: throw IllegalStateException("Group cannot be without position"),
            zoneId = model.zoneId,
            nodes = model.nodes.map { nodeMapper.convertModelToDtoOut(it) }
        )
    }

    override fun convertModelToEntity(model: GroupModel): GroupEntity {
        val zone = zoneRepository.findById(model.zoneId).orElseThrow {
            IllegalArgumentException("Zone with id ${model.zoneId} not found")
        }
        return GroupEntity(
            id = model.id,
            name = model.name,
            position = model.position ?: throw IllegalStateException("Group cannot be without position"),
            zone = zone
        )
    }

    override fun convertEntityToModel(entity: GroupEntity): GroupModel {
        return GroupModel(
            id = entity.id,
            name = entity.name,
            position = entity.position,
            zoneId = entity.zone.id,
            // Avoid loading nodes to prevent circular dependencies
            nodes = emptyList()
        )
    }

    override fun updateEntityFromModel(entity: GroupEntity, model: GroupModel): GroupEntity {
        entity.name = model.name
        // Position is managed by OrderableService, don't update it here
        if (entity.zone.id != model.zoneId) {
            val zone = zoneRepository.findById(model.zoneId).orElseThrow {
                IllegalArgumentException("Zone with id ${model.zoneId} not found")
            }
            entity.zone = zone
        }
        return entity
    }

    override fun extractIdFromModel(model: GroupModel): Long? {
        return model.id
    }

    override fun convertEntityToDto(entity: GroupEntity): GroupDtoOutput {
        return GroupDtoOutput(
            id = entity.id,
            name = entity.name,
            position = entity.position,
            zoneId = entity.zone.id,
            // Avoid loading nodes to prevent circular dependencies
            nodes = emptyList()
        )
    }
}