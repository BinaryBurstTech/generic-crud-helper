package cz.binaryburst.generic.service

import cz.binaryburst.generic.dto.PageResponse
import cz.binaryburst.generic.model.BaseModel
import org.springframework.data.domain.Pageable
import java.io.Serializable

/**
 * Interface defining basic CRUD operations for the service layer.
 *
 * @param ID The type of the entity identifier.
 * @param MODEL The type of the model managed by the service.
 */
interface IBaseService<ID : Serializable, MODEL : BaseModel<ID>> {

    /**
     * Retrieves entities with pagination support.
     *
     * @param pageable Pagination parameters.
     * @return A paginated response of models.
     */
    fun findAll(pageable: Pageable): PageResponse<MODEL>

    /**
     * Retrieves all entities.
     *
     * Note: This method is deprecated. Use findAll(pageable) instead for better performance
     * with large datasets.
     *
     * @return A list of all models.
     */
    @Deprecated(
        "Use findAll(pageable) instead for better performance with large datasets",
        ReplaceWith("findAll(Pageable.unpaged())")
    )
    fun findAll(): List<MODEL>

    /**
     * Creates a new entity.
     *
     * @param model The model representing the new entity.
     * @return The created model.
     * @throws EntityIdAlreadyExistException if an entity with the same ID already exists.
     */
    fun create(model: MODEL): MODEL

    /**
     * Retrieves an entity by its ID.
     *
     * @param id The ID of the entity to retrieve.
     * @return The model of the retrieved entity.
     * @throws EntityNotFoundException if no entity with the specified ID is found.
     */
    fun findById(id: ID): MODEL

    /**
     * Checks if an entity with the given ID exists.
     *
     * @param id The ID to check.
     * @return True if an entity with the given ID exists, false otherwise.
     */
    fun existsById(id: ID): Boolean

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
    fun deleteById(id: ID)

    /**
     * Adds a list of entities.
     *
     * @param models The list of models to add.
     * @return A list of the added models.
     */
    fun addAll(models: List<MODEL>): List<MODEL>

    /**
     * Updates a list of entities.
     *
     * @param models The list of models containing the updated data.
     * @return A list of the updated models.
     */
    fun updateAll(models: List<MODEL>): List<MODEL>

    /**
     * Deletes all entities.
     *
     * Note: This operation should be used with extreme caution as it removes all data.
     */
    @Deprecated("This method poses a significant risk to data integrity. Use with extreme caution.")
    fun deleteAll()
}