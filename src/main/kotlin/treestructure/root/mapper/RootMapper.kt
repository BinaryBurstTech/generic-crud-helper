package cz.binaryburst.treestructure.root.mapper

import cz.binaryburst.generic.mapper.IBaseMapper
import cz.binaryburst.treestructure.root.dto.RootDtoInput
import cz.binaryburst.treestructure.root.dto.RootDtoOutput
import cz.binaryburst.treestructure.root.entity.RootEntity
import cz.binaryburst.treestructure.root.model.RootModel
import cz.binaryburst.treestructure.zone.mapper.ZoneMapper
import org.springframework.stereotype.Component

@Component
class RootMapper(
    private val zoneMapper: ZoneMapper
) : IBaseMapper<RootDtoInput, RootDtoOutput, RootModel, RootEntity, Long> {

    override fun convertDtoToModel(dto: RootDtoInput): RootModel {
        return RootModel(
            id = dto.id,
            name = dto.name
        )
    }

    override fun convertModelToDtoOut(model: RootModel): RootDtoOutput {
        return RootDtoOutput(
            id = model.id,
            name = model.name,
            zones = model.zones.map { zoneMapper.convertModelToDtoOut(it) }
        )
    }

    override fun convertModelToEntity(model: RootModel): RootEntity {
        return RootEntity(
            id = model.id,
            name = model.name
        )
    }

    override fun convertEntityToModel(entity: RootEntity): RootModel {
        return RootModel(
            id = entity.id,
            name = entity.name,
            zones = entity.zones.map { zone ->
                zoneMapper.convertEntityToModel(zone).copy(
                    // Ensure groups are not loaded here to avoid circular dependencies
                    groups = emptyList()
                )
            }
        )
    }

    override fun updateEntityFromModel(entity: RootEntity, model: RootModel): RootEntity {
        entity.name = model.name
        return entity
    }

    override fun extractIdFromModel(model: RootModel): Long? {
        return model.id
    }

    override fun convertEntityToDto(entity: RootEntity): RootDtoOutput {
        return RootDtoOutput(
            id = entity.id,
            name = entity.name,
            zones = entity.zones.map { zoneMapper.convertEntityToDto(it) }
        )
    }
}