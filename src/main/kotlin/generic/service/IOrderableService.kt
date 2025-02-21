package cz.binaryburst.generic.service

import cz.binaryburst.generic.model.OrderableModel
import java.io.Serializable

/**
 * Interface defining basic CRUD operations for the service layer.
 *
 * @param ID The type of the entity identifier.
 * @param MODEL The type of the model managed by the service.
 */
interface IOrderableService<ID : Serializable, PARAMS : OrderableParams, MODEL : OrderableModel<ID>> {

    /**
     * Retrieves all entities.
     *
     * @return A list of all models.
     */
    fun findAllOrderable(params: PARAMS? = null): List<MODEL>

    /**
     * Creates a new entity.
     *
     * @param model The model representing the new entity.
     * @return The created model.
     * @throws EntityIdAlreadyExistException if an entity with the same ID already exists.
     */
    fun createOrderable(model: MODEL, params: PARAMS?= null): MODEL

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
     * Deletes a specific entity by its ID.
     *
     * @param id The ID of the entity to delete.
     */
    fun deleteOrderableById(id: ID, params: PARAMS?= null)

    /**
     * Adds a list of entities.
     *
     * @param models The list of models to add.
     * @return A list of the added models.
     */
    fun addAllOrderable(models: List<MODEL>, params: PARAMS?= null): List<MODEL>

    /**
     * Reorders an entity within the list based on its new position.
     *
     * The method retrieves the entity and its new position, checks if the position
     * needs to be updated, and adjusts the positions of other affected entities
     * accordingly. If the new position is lower, it increments positions of other
     * entities; if higher, it decrements them. The updated entity is then saved.
     *
     * @param model The entity model containing the updated position.
     * @throws EntityNotFoundException If the entity does not exist.
     * @throws Exception If any other error occurs during reordering.
     */

    fun reorder(model: MODEL, params: PARAMS?= null)

    /**
     * Deletes all entities.
     */
    fun deleteOrderableAll(params: PARAMS?= null)
}