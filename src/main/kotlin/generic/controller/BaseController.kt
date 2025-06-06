package cz.binaryburst.generic.controller

import cz.binaryburst.generic.dto.BaseDtoInput
import cz.binaryburst.generic.dto.BaseDtoOutput
import cz.binaryburst.generic.dto.PageResponse
import cz.binaryburst.generic.entity.BaseEntity
import cz.binaryburst.generic.mapper.IBaseMapper
import cz.binaryburst.generic.model.BaseModel
import cz.binaryburst.generic.repository.BaseRepository
import cz.binaryburst.generic.service.BaseService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.Serializable

/**
 * Abstract base class for CRUD controllers.
 * This class provides basic CRUD operations and is intended to be extended by specific controllers.
 *
 * Note: This controller allows exceptions to propagate to the application layer where they should be handled
 * by an appropriate exception handler defined by the application.
 *
 * @param ID The type of the entity identifier.
 * @param DTO_IN The type of the input DTO.
 * @param DTO_OUT The type of the output DTO.
 * @param MODEL The type of the model representing the business logic.
 * @param ENTITY The type of the entity managed by the repository.
 * @param MAPPER The type of the data mapper used for converting between models, entities, and DTOs.
 * @param REPO The type of the repository managing the entity.
 * @param SERVICE The type of the service handling the business logic.
 */
abstract class BaseController<
        ID : Serializable,
        DTO_IN : BaseDtoInput<ID>,
        DTO_OUT : BaseDtoOutput<ID>,
        MODEL : BaseModel<ID>,
        ENTITY : BaseEntity<ID>,
        MAPPER : IBaseMapper<DTO_IN, DTO_OUT, MODEL, ENTITY, ID>,
        REPO : BaseRepository<ENTITY, ID>,
        SERVICE : BaseService<ID, DTO_IN, DTO_OUT, MODEL, ENTITY, REPO, MAPPER>,
        >(
    private val service: SERVICE,
    val mapper: MAPPER
) : IBaseController<ID, DTO_IN, DTO_OUT> {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Retrieves entities with pagination.
     *
     * @param pageable Pagination information.
     * @return A ResponseEntity containing a paginated list of DTO_OUT.
     */
    override fun getAll(pageable: Pageable): ResponseEntity<PageResponse<DTO_OUT>> {
        logger.debug("Entering getAll() with pageable: {}", pageable)

        val pageResponse = service.findAll(pageable)
        val dtoPageResponse = PageResponse(
            content = pageResponse.content.map(mapper::convertModelToDtoOut),
            totalElements = pageResponse.totalElements,
            totalPages = pageResponse.totalPages,
            pageNumber = pageResponse.pageNumber,
            pageSize = pageResponse.pageSize,
            isLast = pageResponse.isLast
        )

        logger.debug("Successfully retrieved page {} of entities", pageable.pageNumber)
        return ResponseEntity(dtoPageResponse, HttpStatus.OK)
    }

    /**
     * Retrieves a specific entity by its ID.
     *
     * @param id The ID of the entity to retrieve.
     * @return A ResponseEntity containing the DTO_OUT of the entity.
     * @throws EntityNotFoundException if no entity with the specified ID is found.
     */
    override fun get(id: ID): ResponseEntity<DTO_OUT> {
        logger.debug("Entering get() with ID: {}", id)

        val model = service.findById(id)
        val dtoOut = mapper.convertModelToDtoOut(model)

        logger.debug("Successfully retrieved entity with ID: {}", id)
        return ResponseEntity(dtoOut, HttpStatus.OK)
    }

    /**
     * Creates a new entity.
     *
     * @param dto The DTO_IN object containing the data to create the entity.
     * @return A ResponseEntity containing the DTO_OUT of the created entity.
     * @throws EntityIdAlreadyExistException if an entity with the same ID already exists.
     * @throws EntityValidationException if validation fails.
     */
    override fun create(dto: DTO_IN): ResponseEntity<DTO_OUT> {
        logger.debug("Entering create() with DTO: {}", dto)

        val model = mapper.convertDtoToModel(dto)
        val createdModel = service.create(model)
        val createdDto = mapper.convertModelToDtoOut(createdModel)

        logger.debug("Successfully created entity with ID: {}", createdDto.id)
        return ResponseEntity(createdDto, HttpStatus.CREATED)
    }

    /**
     * Updates an existing entity by its ID.
     *
     * @param id The ID of the entity to update.
     * @param dto The DTO_IN object containing the updated data.
     * @return A ResponseEntity containing the DTO_OUT of the updated entity.
     * @throws IllegalArgumentException if the path variable ID and DTO ID do not match.
     * @throws EntityNotFoundException if no entity with the specified ID is found.
     * @throws EntityIdNotFoundException if the entity ID is not provided.
     * @throws EntityValidationException if validation fails.
     */
    override fun update(id: ID, dto: DTO_IN): ResponseEntity<DTO_OUT> {
        logger.debug("Entering update() with ID: {} and DTO: {}", id, dto)

        if (id != dto.id) {
            throw IllegalArgumentException("Path variable ID (${id}) and DTO ID (${dto.id}) do not match")
        }

        val model = mapper.convertDtoToModel(dto)
        val updatedModel = service.update(model)
        val updatedDto = mapper.convertModelToDtoOut(updatedModel)

        logger.debug("Successfully updated entity with ID: {}", updatedDto.id)
        return ResponseEntity(updatedDto, HttpStatus.OK)
    }

    /**
     * Deletes a specific entity by its ID.
     *
     * @param id The ID of the entity to delete.
     * @return A ResponseEntity with an HTTP status code.
     * @throws EntityNotFoundException if no entity with the specified ID is found.
     */
    override fun delete(id: ID): ResponseEntity<Unit> {
        logger.debug("Entering delete() with ID: {}", id)

        service.deleteById(id)

        logger.debug("Successfully deleted entity with ID: {}", id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    /**
     * Deletes all entities.
     *
     * Note: This operation should be used with extreme caution as it removes all data.
     *
     * @return A ResponseEntity with an HTTP status code.
     */
    @Deprecated("This method poses a significant risk to data integrity. Use with extreme caution.")
    override fun deleteAll(): ResponseEntity<Unit> {
        logger.warn("CRITICAL OPERATION: Entering deleteAll()")

        service.deleteAll()

        logger.warn("CRITICAL OPERATION COMPLETED: Successfully deleted all entities")
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    /**
     * Adds a list of entities.
     *
     * @param dtos The list of DTO_IN objects to add.
     * @return A ResponseEntity with the created entities.
     * @throws EntityIdAlreadyExistException if any entity with the same ID already exists.
     * @throws EntityValidationException if validation fails for any entity.
     */
    override fun addAll(dtos: List<DTO_IN>): ResponseEntity<List<DTO_OUT>> {
        logger.debug("Entering addAll() with DTOs count: {}", dtos.size)

        val models = dtos.map(mapper::convertDtoToModel)
        val createdModels = service.addAll(models)
        val createdDtos = createdModels.map(mapper::convertModelToDtoOut)

        logger.debug("Successfully added {} entities", createdDtos.size)
        return ResponseEntity(createdDtos, HttpStatus.CREATED)
    }

    /**
     * Updates a list of entities.
     *
     * @param dtos The list of DTO_IN objects containing the updated data.
     * @return A ResponseEntity with the updated entities.
     * @throws EntityNotFoundException if any entity is not found.
     * @throws EntityIdNotFoundException if any entity ID is not provided.
     * @throws EntityValidationException if validation fails for any entity.
     */
    override fun updateAll(dtos: List<DTO_IN>): ResponseEntity<List<DTO_OUT>> {
        logger.debug("Entering updateAll() with DTOs count: {}", dtos.size)

        val models = dtos.map(mapper::convertDtoToModel)
        val updatedModels = service.updateAll(models)
        val updatedDtos = updatedModels.map(mapper::convertModelToDtoOut)

        logger.debug("Successfully updated {} entities", updatedDtos.size)
        return ResponseEntity(updatedDtos, HttpStatus.OK)
    }

}