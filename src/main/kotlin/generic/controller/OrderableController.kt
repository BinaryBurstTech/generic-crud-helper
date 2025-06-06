package cz.binaryburst.generic.controller

import cz.binaryburst.generic.dto.OrderableDtoInput
import cz.binaryburst.generic.dto.OrderableDtoOutput
import cz.binaryburst.generic.dto.PageResponse
import cz.binaryburst.generic.entity.OrderableEntity
import cz.binaryburst.generic.mapper.IOrderableMapper
import cz.binaryburst.generic.model.OrderableModel
import cz.binaryburst.generic.repository.OrderableRepository
import cz.binaryburst.generic.service.OrderableParams
import cz.binaryburst.generic.service.OrderableService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.Serializable

/**
 * Abstract base class for orderable CRUD controllers.
 * This class provides orderable operations and is intended to be extended by specific controllers.
 *
 * Note: This controller allows exceptions to propagate to the application layer where they should be handled
 * by an appropriate exception handler defined by the application.
 *
 * @param ID The type of the entity identifier.
 * @param PARAMS The type of optional parameters for filtering.
 * @param DTO_IN The type of the input DTO.
 * @param DTO_OUT The type of the output DTO.
 * @param MODEL The type of the model representing the business logic.
 * @param ENTITY The type of the entity managed by the repository.
 * @param MAPPER The type of the data mapper used for converting between models, entities, and DTOs.
 * @param REPO The type of the repository managing the entity.
 * @param SERVICE The type of the service handling the business logic.
 */
abstract class OrderableController<
        ID : Serializable,
        PARAMS : OrderableParams,
        DTO_IN : OrderableDtoInput<ID>,
        DTO_OUT : OrderableDtoOutput<ID>,
        MODEL : OrderableModel<ID>,
        ENTITY : OrderableEntity<ID>,
        MAPPER : IOrderableMapper<DTO_IN, DTO_OUT, MODEL, ENTITY, ID>,
        REPO : OrderableRepository<ENTITY, ID>,
        SERVICE : OrderableService<ID, PARAMS, DTO_IN, DTO_OUT, MODEL, ENTITY, REPO, MAPPER>
        >(
    private val orderableService: SERVICE,
    mapper: MAPPER
) : BaseController<ID, DTO_IN, DTO_OUT, MODEL, ENTITY, MAPPER, REPO, SERVICE>(orderableService, mapper),
    IOrderableController<ID, PARAMS, DTO_IN, DTO_OUT> {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Retrieves entities with ordering support and pagination.
     *
     * @param pageable Pagination information.
     * @param params Optional parameters for filtering.
     * @return A ResponseEntity containing a paginated list of DTO_OUT sorted by position.
     */
    override fun getAllOrderable(pageable: Pageable, params: PARAMS?): ResponseEntity<PageResponse<DTO_OUT>> {
        logger.debug("Entering getAllOrderable() with pageable: {} and params: {}", pageable, params)

        val pageResponse = orderableService.findAllOrderable(pageable, params)
        val dtoPageResponse = PageResponse(
            content = pageResponse.content.map(mapper::convertModelToDtoOut),
            totalElements = pageResponse.totalElements,
            totalPages = pageResponse.totalPages,
            pageNumber = pageResponse.pageNumber,
            pageSize = pageResponse.pageSize,
            isLast = pageResponse.isLast
        )

        logger.debug("Successfully retrieved page {} of orderable entities", pageable.pageNumber)
        return ResponseEntity(dtoPageResponse, HttpStatus.OK)
    }

    /**
     * Creates a new entity with position management.
     *
     * @param dto The DTO_IN object containing the data to create the entity.
     * @param params Optional parameters for context.
     * @return A ResponseEntity containing the DTO_OUT of the created entity.
     * @throws EntityIdAlreadyExistException if an entity with the same ID already exists.
     * @throws EntityValidationException if validation fails.
     */
    override fun createOrderable(dto: DTO_IN, params: PARAMS?): ResponseEntity<DTO_OUT> {
        logger.debug("Entering createOrderable() with DTO: {} and params: {}", dto, params)

        val model = mapper.convertDtoToModel(dto)
        val createdModel = orderableService.createOrderable(model, params)
        val createdDto = mapper.convertModelToDtoOut(createdModel)

        logger.debug("Successfully created orderable entity with ID: {}", createdDto.id)
        return ResponseEntity(createdDto, HttpStatus.CREATED)
    }

    /**
     * Deletes a specific entity by its ID and updates positions of remaining entities.
     *
     * @param id The ID of the entity to delete.
     * @param params Optional parameters for context.
     * @return A ResponseEntity with an HTTP status code.
     * @throws EntityNotFoundException if no entity with the specified ID is found.
     */
    override fun deleteOrderableById(id: ID, params: PARAMS?): ResponseEntity<Unit> {
        logger.debug("Entering deleteOrderableById() with ID: {} and params: {}", id, params)

        orderableService.deleteOrderableById(id, params)

        logger.debug("Successfully deleted orderable entity with ID: {}", id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    /**
     * Adds a list of entities with position handling.
     *
     * @param dtos The list of DTO_IN objects to add.
     * @param params Optional parameters for context.
     * @return A ResponseEntity with the created entities.
     * @throws EntityIdAlreadyExistException if any entity with the same ID already exists.
     * @throws EntityValidationException if validation fails for any entity.
     */
    override fun addAllOrderable(dtos: List<DTO_IN>, params: PARAMS?): ResponseEntity<List<DTO_OUT>> {
        logger.debug("Entering addAllOrderable() with DTOs count: {} and params: {}", dtos.size, params)

        val models = dtos.map(mapper::convertDtoToModel)
        val createdModels = orderableService.addAllOrderable(models, params)
        val createdDtos = createdModels.map(mapper::convertModelToDtoOut)

        logger.debug("Successfully added {} orderable entities", createdDtos.size)
        return ResponseEntity(createdDtos, HttpStatus.CREATED)
    }

    /**
     * Reorders an entity within the list based on its new position.
     *
     * @param dto The DTO_IN object containing the updated position.
     * @param params Optional parameters for context.
     * @return A ResponseEntity containing the DTO_OUT of the reordered entity.
     * @throws EntityNotFoundException if no entity with the specified ID is found.
     * @throws EntityIdNotFoundException if the entity ID is not provided.
     * @throws EntityValidationException if validation fails.
     */
    override fun reorder(dto: DTO_IN, params: PARAMS?): ResponseEntity<DTO_OUT> {
        logger.debug("Entering reorder() with DTO: {} and params: {}", dto, params)

        val model = mapper.convertDtoToModel(dto)
        orderableService.reorder(model, params)
        val updatedModel = orderableService.findById(model.id)
        val updatedDto = mapper.convertModelToDtoOut(updatedModel)

        logger.debug("Successfully reordered entity with ID: {}", updatedDto.id)
        return ResponseEntity(updatedDto, HttpStatus.OK)
    }

    /**
     * Deletes all entities.
     *
     * Note: This operation should be used with extreme caution as it removes all data.
     *
     * @param params Optional parameters for context.
     * @return A ResponseEntity with an HTTP status code.
     */
    @Deprecated("This method poses a significant risk to data integrity. Use with extreme caution.")
    override fun deleteAllOrderable(params: PARAMS?): ResponseEntity<Unit> {
        logger.warn("CRITICAL OPERATION: Entering deleteAllOrderable() with params: {}", params)

        orderableService.deleteOrderableAll(params)

        logger.warn("CRITICAL OPERATION COMPLETED: Successfully deleted all orderable entities")
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}