package cz.binaryburst.generic.mapper

import cz.binaryburst.generic.dto.BasePartialDtoInput
import cz.binaryburst.generic.dto.BasePartialDtoOutput
import cz.binaryburst.generic.entity.BasePartialEntity
import cz.binaryburst.generic.model.BasePartialModel

/**
 * Interface for mapping between DTOs, models, and entities.
 *
 * @param PARTIAL_DTO_IN The type of the input DTO.
 * @param PARTIAL_DTO_OUT The type of the output DTO.
 * @param PARTIAL_MODEL The type of the model.
 * @param PARTIAL_ENTITY The type of the entity.
 */
interface IBasePartialMapper<
        PARTIAL_DTO_IN : BasePartialDtoInput,
        PARTIAL_DTO_OUT : BasePartialDtoOutput,
        PARTIAL_MODEL : BasePartialModel,
        PARTIAL_ENTITY : BasePartialEntity> {

    /**
     * Converts an input DTO to a model.
     *
     * @param dto The input DTO to convert.
     * @return The converted model.
     */
    fun convertDtoToModel(dto: PARTIAL_DTO_IN): PARTIAL_MODEL

    /**
     * Converts a model to an output DTO.
     *
     * @param model The model to convert.
     * @return The converted output DTO.
     */
    fun convertModelToDtoOut(model: PARTIAL_MODEL): PARTIAL_DTO_OUT

    /**
     * Converts a model to an entity.
     *
     * @param model The model to convert.
     * @return The converted entity.
     */
    fun convertModelToEntity(model: PARTIAL_MODEL): PARTIAL_ENTITY

    /**
     * Converts an entity to a model.
     *
     * @param entity The entity to convert.
     * @return The converted model.
     */
    fun convertEntityToModel(entity: PARTIAL_ENTITY): PARTIAL_MODEL

    /**
     * Updates an existing entity from a model.
     *
     * @param entity The entity to update.
     * @param model The model containing updated data.
     * @return The updated entity.
     */
    fun updateEntityFromModel(entity: PARTIAL_ENTITY, model: PARTIAL_MODEL): PARTIAL_ENTITY

    /**
     * Converts an entity to an output DTO.
     *
     * @param entity The entity to convert.
     * @return The converted output DTO.
     */
    fun convertEntityToDto(entity: PARTIAL_ENTITY): PARTIAL_DTO_OUT
}
