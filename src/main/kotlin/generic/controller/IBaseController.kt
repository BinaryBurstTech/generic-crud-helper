package cz.binaryburst.generic.controller

import org.springframework.http.ResponseEntity
import java.io.Serializable

/**
 * Interface defining basic CRUD operations for controllers.
 *
 * @param ID The type of the entity identifier.
 * @param DTO_IN The type of the input DTO.
 * @param DTO_OUT The type of the output DTO.
 */
interface IBaseController<ID : Serializable, DTO_IN, DTO_OUT> {

    /**
     * Retrieves all entities.
     *
     * @return A ResponseEntity containing a list of all DTO_OUT or an HTTP status code.
     */
    fun getAll(): ResponseEntity<List<DTO_OUT>>

    /**
     * Retrieves a specific entity by its ID.
     *
     * @param id The ID of the entity to retrieve.
     * @return A ResponseEntity containing the DTO_OUT of the entity or an HTTP status code.
     */
    fun get(id: ID): ResponseEntity<DTO_OUT>

    /**
     * Creates a new entity.
     *
     * @param dto The DTO_IN object containing the data to create the entity.
     * @return A ResponseEntity containing the DTO_OUT of the created entity or an HTTP status code.
     */
    fun create(dto: DTO_IN): ResponseEntity<DTO_OUT>

    /**
     * Updates an existing entity by its ID.
     *
     * @param id The ID of the entity to update.
     * @param dto The DTO_IN object containing the updated data.
     * @return A ResponseEntity containing the DTO_OUT of the updated entity or an HTTP status code.
     */
    fun update(id: ID, dto: DTO_IN): ResponseEntity<DTO_OUT>

    /**
     * Deletes a specific entity by its ID.
     *
     * @param id The ID of the entity to delete.
     * @return A ResponseEntity with an HTTP status code.
     */
    fun delete(id: ID): ResponseEntity<Unit>

    /**
     * Deletes all entities.
     *
     * @return A ResponseEntity with an HTTP status code.
     */
    fun deleteAll(): ResponseEntity<Unit>

    /**
     * Adds a list of entities.
     *
     * @param dtos The list of DTO_IN objects to add.
     * @return A ResponseEntity with an HTTP status code.
     */
    fun addAll(dtos: List<DTO_IN>): ResponseEntity<List<DTO_OUT>>

    /**
     * Updates a list of entities.
     *
     * @param dtos The list of DTO_IN objects containing the updated data.
     * @return A ResponseEntity with an HTTP status code.
     */
    fun updateAll(dtos: List<DTO_IN>): ResponseEntity<List<DTO_OUT>>
}
