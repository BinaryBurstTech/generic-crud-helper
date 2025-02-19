package cz.binaryburst.generic.mapper

import cz.binaryburst.generic.dto.OrderableDtoInput
import cz.binaryburst.generic.dto.OrderableDtoOutput
import cz.binaryburst.generic.entity.OrderableEntity
import cz.binaryburst.generic.model.OrderableModel
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
interface IOrderableMapper<
        DTO_IN : OrderableDtoInput<ID>,
        DTO_OUT : OrderableDtoOutput<ID>,
        MODEL : OrderableModel<ID>,
        ENTITY : OrderableEntity<ID>,
        ID : Serializable> : IBaseMapper<DTO_IN, DTO_OUT, MODEL, ENTITY, ID> {

    /**
     * Converts an input DTO to a model.
     *
     * @param dto The input DTO to convert.
     * @return The converted model.
     */
    override fun convertDtoToModel(dto: DTO_IN): MODEL

    /**
     * Converts a model to an output DTO.
     *
     * @param model The model to convert.
     * @return The converted output DTO.
     */
    override fun convertModelToDtoOut(model: MODEL): DTO_OUT

    /**
     * Converts a model to an entity.
     *
     * @param model The model to convert.
     * @return The converted entity.
     */
    override fun convertModelToEntity(model: MODEL): ENTITY

    /**
     * Converts an entity to a model.
     *
     * @param entity The entity to convert.
     * @return The converted model.
     */
    override fun convertEntityToModel(entity: ENTITY): MODEL

    /**
     * Updates an existing entity from a model.
     *
     * @param entity The entity to update.
     * @param model The model containing updated data.
     * @return The updated entity.
     */
    override fun updateEntityFromModel(entity: ENTITY, model: MODEL): ENTITY

    /**
     * Extracts the identifier from a model.
     *
     * @param model The model from which to extract the ID.
     * @return The extracted ID, or null if not available.
     */
    override fun extractIdFromModel(model: MODEL): ID?

    /**
     * Converts an entity to an output DTO.
     *
     * @param entity The entity to convert.
     * @return The converted output DTO.
     */
    override fun convertEntityToDto(entity: ENTITY): DTO_OUT
}
