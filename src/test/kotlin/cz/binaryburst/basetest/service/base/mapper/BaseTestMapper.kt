package cz.binaryburst.basetest.service.base.mapper

import cz.binaryburst.basetest.service.base.dto.BaseTestDtoInput
import cz.binaryburst.basetest.service.base.dto.BaseTestDtoOutput
import cz.binaryburst.basetest.service.base.entity.BaseTestEntity
import cz.binaryburst.basetest.service.base.model.BaseTestModel
import cz.binaryburst.generic.mapper.IBaseMapper
import org.springframework.stereotype.Component

@Component
class BaseTestMapper : IBaseMapper<BaseTestDtoInput, BaseTestDtoOutput, BaseTestModel, BaseTestEntity, Long> {
    override fun convertDtoToModel(dto: BaseTestDtoInput): BaseTestModel {
        return BaseTestModel(
            id = dto.id,
            name = dto.name
        )
    }

    override fun convertModelToDtoOut(model: BaseTestModel): BaseTestDtoOutput {
        return BaseTestDtoOutput(
            id = model.id,
            name = model.name
        )
    }

    override fun convertModelToEntity(model: BaseTestModel): BaseTestEntity {
        return BaseTestEntity(
            id = model.id,
            name = model.name
        )
    }

    override fun convertEntityToModel(entity: BaseTestEntity): BaseTestModel {
        return BaseTestModel(
            id = entity.id,
            name = entity.name
        )
    }

    override fun updateEntityFromModel(entity: BaseTestEntity, model: BaseTestModel): BaseTestEntity {
        entity.name = model.name
        return entity
    }

    override fun extractIdFromModel(model: BaseTestModel): Long? {
        return model.id
    }

    override fun convertEntityToDto(entity: BaseTestEntity): BaseTestDtoOutput {
        return BaseTestDtoOutput(
            id = entity.id,
            name = entity.name
        )
    }
}