package cz.binaryburst.basetest.service.orderable.mapper


import cz.binaryburst.basetest.service.orderable.dto.OrderableTestDtoInput
import cz.binaryburst.basetest.service.orderable.dto.OrderableTestDtoOutput
import cz.binaryburst.basetest.service.orderable.entity.OrderableTestEntity
import cz.binaryburst.basetest.service.orderable.model.OrderableTestModel
import cz.binaryburst.generic.mapper.IOrderableMapper
import org.springframework.stereotype.Component

@Component
class OrderableTestMapper :
    IOrderableMapper<OrderableTestDtoInput, OrderableTestDtoOutput, OrderableTestModel, OrderableTestEntity, Long> {

    override fun convertDtoToModel(dto: OrderableTestDtoInput): OrderableTestModel {
        return OrderableTestModel(
            id = dto.id,
            name = dto.name,
            position = dto.position
        )
    }

    override fun convertModelToDtoOut(model: OrderableTestModel): OrderableTestDtoOutput {
        return OrderableTestDtoOutput(
            id = model.id,
            name = model.name,
            position = model.position ?: throw IllegalStateException("Item cannot be without position")
        )
    }

    override fun convertModelToEntity(model: OrderableTestModel): OrderableTestEntity {
        return OrderableTestEntity(
            id = model.id,
            name = model.name,
            position = model.position ?: throw IllegalStateException("Item cannot be without position")
        )
    }

    override fun convertEntityToModel(entity: OrderableTestEntity): OrderableTestModel {
        return OrderableTestModel(
            id = entity.id,
            name = entity.name,
            position = entity.position
        )
    }

    override fun updateEntityFromModel(entity: OrderableTestEntity, model: OrderableTestModel): OrderableTestEntity {
        entity.name = model.name
        return entity
    }

    override fun extractIdFromModel(model: OrderableTestModel): Long? {
        return model.id
    }

    override fun convertEntityToDto(entity: OrderableTestEntity): OrderableTestDtoOutput {
        return OrderableTestDtoOutput(
            id = entity.id,
            name = entity.name,
            position = entity.position
        )
    }
}