package cz.binaryburst.basetest.service.mapper

import cz.binaryburst.basetest.service.dto.BaseTestInsertDto
import cz.binaryburst.basetest.service.dto.BaseTestOutputDto
import cz.binaryburst.basetest.service.entity.BaseTestEntity
import cz.binaryburst.basetest.service.model.BaseTestModel
import cz.binaryburst.generic.mapper.IDataMapper
import org.springframework.stereotype.Component

@Component
class BaseTestMapper : IDataMapper<BaseTestInsertDto, BaseTestOutputDto, BaseTestModel, BaseTestEntity, Long> {
    override fun convertDtoToModel(dto: BaseTestInsertDto): BaseTestModel {
        return BaseTestModel(
            id = dto.id,
            name = dto.name
        )
    }

    override fun convertModelToDtoOut(model: BaseTestModel): BaseTestOutputDto {
        return BaseTestOutputDto(
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

    override fun convertEntityToDto(entity: BaseTestEntity): BaseTestOutputDto {
        return BaseTestOutputDto(
            id = entity.id,
            name = entity.name
        )
    }
}