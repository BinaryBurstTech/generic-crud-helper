package cz.binaryburst.treestructure.zone.mapper

import cz.binaryburst.generic.mapper.IBaseMapper
import cz.binaryburst.treestructure.group.mapper.GroupMapper
import cz.binaryburst.treestructure.root.repository.RootRepository
import cz.binaryburst.treestructure.zone.dto.ZoneDtoInput
import cz.binaryburst.treestructure.zone.dto.ZoneDtoOutput
import cz.binaryburst.treestructure.zone.entity.ZoneEntity
import cz.binaryburst.treestructure.zone.model.ZoneModel
import org.springframework.stereotype.Component

@Component
class ZoneMapper(
    private val groupMapper: GroupMapper,
    private val rootRepository: RootRepository
) : IBaseMapper<ZoneDtoInput, ZoneDtoOutput, ZoneModel, ZoneEntity, Long> {

    override fun convertDtoToModel(dto: ZoneDtoInput): ZoneModel {
        return ZoneModel(
            id = dto.id,
            name = dto.name,
            rootId = dto.rootId
        )
    }

    override fun convertModelToDtoOut(model: ZoneModel): ZoneDtoOutput {
        return ZoneDtoOutput(
            id = model.id,
            name = model.name,
            rootId = model.rootId,
            groups = model.groups.map { groupMapper.convertModelToDtoOut(it) }
        )
    }

    override fun convertModelToEntity(model: ZoneModel): ZoneEntity {
        val root = rootRepository.findById(model.rootId).orElseThrow {
            IllegalArgumentException("Root with id ${model.rootId} not found")
        }
        return ZoneEntity(
            id = model.id,
            name = model.name,
            root = root
        )
    }

    override fun convertEntityToModel(entity: ZoneEntity): ZoneModel {
        return ZoneModel(
            id = entity.id,
            name = entity.name,
            rootId = entity.root.id,
            groups = entity.groups.map { group ->
                groupMapper.convertEntityToModel(group).copy(
                    // Ensure nodes are not loaded here to avoid circular dependencies
                    nodes = emptyList()
                )
            }
        )
    }

    override fun updateEntityFromModel(entity: ZoneEntity, model: ZoneModel): ZoneEntity {
        entity.name = model.name
        if (entity.root.id != model.rootId) {
            val root = rootRepository.findById(model.rootId).orElseThrow {
                IllegalArgumentException("Root with id ${model.rootId} not found")
            }
            entity.root = root
        }
        return entity
    }

    override fun extractIdFromModel(model: ZoneModel): Long? {
        return model.id
    }

    override fun convertEntityToDto(entity: ZoneEntity): ZoneDtoOutput {
        return ZoneDtoOutput(
            id = entity.id,
            name = entity.name,
            rootId = entity.root.id,
            groups = entity.groups.map { groupMapper.convertEntityToDto(it) }
        )
    }
}