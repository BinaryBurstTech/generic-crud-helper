package cz.binaryburst.generic.mapper

import cz.binaryburst.generic.dto.BaseInsertDto
import cz.binaryburst.generic.dto.BaseOutputDto
import cz.binaryburst.generic.entity.BaseEntity
import cz.binaryburst.generic.model.BaseModel
import java.io.Serializable

interface IDataMapper<DTO_IN : BaseInsertDto<ID>, DTO_OUT : BaseOutputDto<ID>, MODEL : BaseModel<ID>, ENTITY : BaseEntity<ID>, ID : Serializable> {
    fun convertDtoToModel(dto: DTO_IN): MODEL
    fun convertModelToDtoOut(model: MODEL): DTO_OUT
    fun convertModelToEntity(model: MODEL): ENTITY
    fun convertEntityToModel(entity: ENTITY): MODEL
    fun updateEntityFromModel(entity: ENTITY, model: MODEL): ENTITY
    fun extractIdFromModel(model: MODEL): ID?
    fun convertEntityToDto(entity: ENTITY): DTO_OUT
}
