package cz.binaryburst.generic.mapper

import cz.binaryburst.generic.dto.BaseDtoInput
import cz.binaryburst.generic.dto.BaseDtoOutput
import cz.binaryburst.generic.entity.BaseEntity
import cz.binaryburst.generic.model.BaseModel
import java.io.Serializable

/**
 * Interface for mapping between DTOs, models, and entities.
 *
 * @param DTO_IN The type of the input DTO.
 * @param DTO_OUT The type of the output DTO.
 * @param MODEL The type of the model.
 * @param ENTITY The type of the entity.
 * @param ID The type of the entity identifier.
 */
interface IBaseMapper<
        DTO_IN : BaseDtoInput<ID>,
        DTO_OUT : BaseDtoOutput<ID>,
        MODEL : BaseModel<ID>,
        ENTITY : BaseEntity<ID>,
        ID : Serializable> {

    /**
     * Converts an input DTO to a model.
     *
     * @param dto The input DTO to convert.
     * @return The converted model.
     */
    fun convertDtoToModel(dto: DTO_IN): MODEL

    /**
     * Converts a model to an output DTO.
     *
     * @param model The model to convert.
     * @return The converted output DTO.
     */
    fun convertModelToDtoOut(model: MODEL): DTO_OUT

    /**
     * Converts a model to an entity.
     *
     * @param model The model to convert.
     * @return The converted entity.
     */
    fun convertModelToEntity(model: MODEL): ENTITY

    /**
     * Converts an entity to a model.
     *
     * @param entity The entity to convert.
     * @return The converted model.
     */
    fun convertEntityToModel(entity: ENTITY): MODEL

    /**
     * Updates an existing entity from a model.
     *
     * @param entity The entity to update.
     * @param model The model containing updated data.
     * @return The updated entity.
     */
    fun updateEntityFromModel(entity: ENTITY, model: MODEL): ENTITY

    /**
     * Extracts the identifier from a model.
     *
     * @param model The model from which to extract the ID.
     * @return The extracted ID, or null if not available.
     */
    fun extractIdFromModel(model: MODEL): ID?

    /**
     * Converts an entity to an output DTO.
     *
     * @param entity The entity to convert.
     * @return The converted output DTO.
     */
    fun convertEntityToDto(entity: ENTITY): DTO_OUT
}
