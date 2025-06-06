package cz.binaryburst.generic.controller

import cz.binaryburst.generic.dto.PageResponse
import cz.binaryburst.generic.service.OrderableParams
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import java.io.Serializable

/**
 * Interface defining orderable CRUD operations for controllers.
 *
 * @param ID The type of the entity identifier.
 * @param PARAMS The type of optional parameters for filtering.
 * @param DTO_IN The type of the input DTO.
 * @param DTO_OUT The type of the output DTO.
 */
interface IOrderableController<ID : Serializable, PARAMS : OrderableParams, DTO_IN, DTO_OUT> : IBaseController<ID, DTO_IN, DTO_OUT> {

    /**
     * Retrieves entities with ordering support and pagination.
     *
     * @param pageable Pagination information.
     * @param params Optional parameters for filtering.
     * @return A ResponseEntity containing a paginated list of DTO_OUT sorted by position.
     */
    fun getAllOrderable(pageable: Pageable, params: PARAMS?): ResponseEntity<PageResponse<DTO_OUT>>

    /**
     * Creates a new entity with position management.
     *
     * @param dto The DTO_IN object containing the data to create the entity.
     * @param params Optional parameters for context.
     * @return A ResponseEntity containing the DTO_OUT of the created entity.
     */
    fun createOrderable(dto: DTO_IN, params: PARAMS?): ResponseEntity<DTO_OUT>

    /**
     * Deletes a specific entity by its ID and updates positions of remaining entities.
     *
     * @param id The ID of the entity to delete.
     * @param params Optional parameters for context.
     * @return A ResponseEntity with an HTTP status code.
     */
    fun deleteOrderableById(id: ID, params: PARAMS?): ResponseEntity<Unit>

    /**
     * Adds a list of entities with position handling.
     *
     * @param dtos The list of DTO_IN objects to add.
     * @param params Optional parameters for context.
     * @return A ResponseEntity with the created entities.
     */
    fun addAllOrderable(dtos: List<DTO_IN>, params: PARAMS?): ResponseEntity<List<DTO_OUT>>

    /**
     * Reorders an entity within the list based on its new position.
     *
     * @param dto The DTO_IN object containing the updated position.
     * @param params Optional parameters for context.
     * @return A ResponseEntity containing the DTO_OUT of the reordered entity.
     */
    fun reorder(dto: DTO_IN, params: PARAMS?): ResponseEntity<DTO_OUT>

    /**
     * Deletes all entities.
     *
     * Note: This operation should be used with extreme caution as it removes all data.
     *
     * @param params Optional parameters for context.
     * @return A ResponseEntity with an HTTP status code.
     */
    @Deprecated("This method poses a significant risk to data integrity. Use with extreme caution.")
    fun deleteAllOrderable(params: PARAMS?): ResponseEntity<Unit>
}