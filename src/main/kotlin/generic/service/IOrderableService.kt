package cz.binaryburst.generic.service

import cz.binaryburst.generic.dto.PageResponse
import cz.binaryburst.generic.model.OrderableModel
import org.springframework.data.domain.Pageable
import java.io.Serializable

/**
 * Interface defining basic CRUD operations for the service layer with ordering support.
 *
 * @param ID The type of the entity identifier.
 * @param PARAMS The type of optional parameters for filtering.
 * @param MODEL The type of the model managed by the service.
 */
interface IOrderableService<ID : Serializable, PARAMS : OrderableParams, MODEL : OrderableModel<ID>> {

    /**
     * Retrieves all entities sorted by position with pagination support.
     *
     * @param pageable Pagination information.
     * @param params Optional parameters for filtering.
     * @return A paginated response of models sorted by position.
     */
    fun findAllOrderable(pageable: Pageable, params: PARAMS? = null): PageResponse<MODEL>

    /**
     * Retrieves all entities sorted by position.
     *
     * @param params Optional parameters for filtering.
     * @return A list of models sorted by position.
     */
    @Deprecated(
        "Use findAllOrderable(pageable, params) for better performance with large datasets",
        ReplaceWith("findAllOrderable(Pageable.unpaged(), params)")
    )
    fun findAllOrderable(params: PARAMS? = null): List<MODEL>

    /**
     * Creates a new entity and manages its position.
     *
     * @param model The model representing the entity to create.
     * @param params Optional parameters for context.
     * @return The created model.
     * @throws EntityIdAlreadyExistException if an entity with the same ID already exists.
     */
    fun createOrderable(model: MODEL, params: PARAMS? = null): MODEL

    /**
     * Retrieves an entity by its ID.
     *
     * @param id The ID of the entity to retrieve.
     * @return The model of the retrieved entity.
     * @throws EntityNotFoundException if no entity with the specified ID is found.
     */
    fun findById(id: ID): MODEL

    /**
     * Updates an existing entity.
     *
     * @param model The model containing the updated data.
     * @return The updated model.
     * @throws EntityIdNotFoundException if the entity's ID is not provided.
     * @throws EntityNotFoundException if no entity with the specified ID is found.
     */
    fun update(model: MODEL): MODEL

    /**
     * Deletes a specific entity by its ID and updates positions of remaining entities.
     *
     * @param id The ID of the entity to delete.
     * @param params Optional parameters for context.
     * @throws EntityNotFoundException if no entity with the given ID is found.
     */
    fun deleteOrderableById(id: ID, params: PARAMS? = null)

    /**
     * Adds a list of entities with position handling.
     *
     * @param models The list of models to add.
     * @param params Optional parameters for context.
     * @return A list of the added models.
     */
    fun addAllOrderable(models: List<MODEL>, params: PARAMS? = null): List<MODEL>

    /**
     * Reorders an entity within the list based on its new position.
     *
     * The method retrieves the entity and its new position, checks if the position
     * needs to be updated, and adjusts the positions of other affected entities
     * accordingly. If the new position is lower, it increments positions of other
     * entities; if higher, it decrements them. The updated entity is then saved.
     *
     * @param model The entity model containing the updated position.
     * @param params Optional parameters for context.
     * @throws EntityNotFoundException If the entity does not exist.
     * @throws Exception If any other error occurs during reordering.
     */
    fun reorder(model: MODEL, params: PARAMS? = null)

    /**
     * Deletes all entities.
     *
     * Note: This operation should be used with extreme caution as it removes all data.
     *
     * @param params Optional parameters for context.
     */
    @Deprecated("This method poses a significant risk to data integrity. Use with extreme caution.")
    fun deleteOrderableAll(params: PARAMS? = null)
}