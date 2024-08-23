package cz.binaryburst.basetest.service.embeddable.mapper


import cz.binaryburst.basetest.service.embeddable.dto.EmbeddedTestDtoInput
import cz.binaryburst.basetest.service.embeddable.dto.EmbeddedTestDtoOutput
import cz.binaryburst.basetest.service.embeddable.entity.EmbeddedTestEntity
import cz.binaryburst.basetest.service.embeddable.model.EmbeddedTestModel
import cz.binaryburst.generic.mapper.IBaseMapper
import org.springframework.stereotype.Component

@Component
class EmbeddedTestMapper(
    private val embeddedPartialTestMapper: EmbeddedPartialTestMapper
) :
    IBaseMapper<EmbeddedTestDtoInput, EmbeddedTestDtoOutput, EmbeddedTestModel, EmbeddedTestEntity, Long> {
    override fun convertDtoToModel(dto: EmbeddedTestDtoInput): EmbeddedTestModel {
        return EmbeddedTestModel(
            id = dto.id,
            name = dto.name,
            embeddedChildTestModel = embeddedPartialTestMapper.convertDtoToModel(dto.embeddedChildTestInsertDto)
        )
    }

    override fun convertModelToDtoOut(model: EmbeddedTestModel): EmbeddedTestDtoOutput {
        return EmbeddedTestDtoOutput(
            id = model.id,
            name = model.name,
            embeddedChildTestOutputDto = embeddedPartialTestMapper.convertModelToDtoOut(model.embeddedChildTestModel)
        )
    }

    override fun convertModelToEntity(model: EmbeddedTestModel): EmbeddedTestEntity {
        return EmbeddedTestEntity(
            id = model.id,
            name = model.name,
            embeddedChildTestEntity = embeddedPartialTestMapper.convertModelToEntity(model.embeddedChildTestModel)
        )
    }

    override fun convertEntityToModel(entity: EmbeddedTestEntity): EmbeddedTestModel {
        return EmbeddedTestModel(
            id = entity.id,
            name = entity.name,
            embeddedChildTestModel = embeddedPartialTestMapper.convertEntityToModel(entity.embeddedChildTestEntity)
        )
    }

    override fun updateEntityFromModel(entity: EmbeddedTestEntity, model: EmbeddedTestModel): EmbeddedTestEntity {
        entity.name = model.name
        entity.embeddedChildTestEntity = embeddedPartialTestMapper.updateEntityFromModel(
            entity.embeddedChildTestEntity,
            model.embeddedChildTestModel
        )
        return entity
    }

    override fun extractIdFromModel(model: EmbeddedTestModel): Long? {
        return model.id
    }

    override fun convertEntityToDto(entity: EmbeddedTestEntity): EmbeddedTestDtoOutput {
        return EmbeddedTestDtoOutput(
            id = entity.id,
            name = entity.name,
            embeddedChildTestOutputDto = embeddedPartialTestMapper.convertEntityToDto(entity.embeddedChildTestEntity)
        )
    }
}