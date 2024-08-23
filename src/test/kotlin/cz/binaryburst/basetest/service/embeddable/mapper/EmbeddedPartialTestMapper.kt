package cz.binaryburst.basetest.service.embeddable.mapper


import cz.binaryburst.basetest.service.embeddable.dto.partial.EmbeddedChildTestInsertDto
import cz.binaryburst.basetest.service.embeddable.dto.partial.EmbeddedChildTestOutputDto
import cz.binaryburst.basetest.service.embeddable.entity.partial.EmbeddedChildTestEntity
import cz.binaryburst.basetest.service.embeddable.model.partial.EmbeddedChildTestModel
import cz.binaryburst.generic.mapper.IBasePartialMapper
import org.springframework.stereotype.Component

@Component
class EmbeddedPartialTestMapper :
    IBasePartialMapper<EmbeddedChildTestInsertDto, EmbeddedChildTestOutputDto, EmbeddedChildTestModel, EmbeddedChildTestEntity> {

    override fun convertDtoToModel(dto: EmbeddedChildTestInsertDto): EmbeddedChildTestModel {
        return EmbeddedChildTestModel(
            name = dto.name
        )
    }

    override fun convertModelToDtoOut(model: EmbeddedChildTestModel): EmbeddedChildTestOutputDto {
        return EmbeddedChildTestOutputDto(
            name = model.name
        )
    }

    override fun convertModelToEntity(model: EmbeddedChildTestModel): EmbeddedChildTestEntity {
        return EmbeddedChildTestEntity(
            value = model.name
        )
    }

    override fun convertEntityToModel(entity: EmbeddedChildTestEntity): EmbeddedChildTestModel {
        return EmbeddedChildTestModel(
            name = entity.value
        )
    }

    override fun updateEntityFromModel(
        entity: EmbeddedChildTestEntity,
        model: EmbeddedChildTestModel
    ): EmbeddedChildTestEntity {
        entity.value = model.name
        return entity
    }

    override fun convertEntityToDto(entity: EmbeddedChildTestEntity): EmbeddedChildTestOutputDto {
        return EmbeddedChildTestOutputDto(
            name = entity.value
        )
    }
}