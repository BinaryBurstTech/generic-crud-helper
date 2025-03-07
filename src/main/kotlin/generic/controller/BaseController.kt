package cz.binaryburst.generic.controller

import cz.binaryburst.generic.dto.BaseDtoInput
import cz.binaryburst.generic.dto.BaseDtoOutput
import cz.binaryburst.generic.dto.PageResponse
import cz.binaryburst.generic.entity.BaseEntity
import cz.binaryburst.generic.exception.EntityNotFoundException
import cz.binaryburst.generic.exception.EntityValidationException
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
    private val mapper: MAPPER
) : IBaseController<ID, DTO_IN, DTO_OUT> {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Retrieves entities with pagination.
     *
     * @param pageable Pagination information.
     * @return A ResponseEntity containing a paginated list of DTO_OUT or an HTTP status code.
     */
    override fun getAll(pageable: Pageable): ResponseEntity<PageResponse<DTO_OUT>> {
        logger.debug("Entering getAll() with pageable: {}", pageable)
        return try {
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
            ResponseEntity(dtoPageResponse, HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Error occurred while fetching entities with pagination", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } finally {
            logger.debug("Exiting getAll()")
        }
    }

    /**
     * Retrieves a specific entity by its ID.
     *
     * @param id The ID of the entity to retrieve.
     * @return A ResponseEntity containing the DTO_OUT of the entity or an HTTP status code.
     */
    override fun get(id: ID): ResponseEntity<DTO_OUT> {
        logger.debug("Entering get() with ID: {}", id)
        return try {
            val dtoOut = service.findById(id).let(mapper::convertModelToDtoOut)
            logger.debug("Successfully retrieved entity with ID: {}", id)
            ResponseEntity(dtoOut, HttpStatus.OK)
        } catch (e: EntityNotFoundException) {
            logger.warn("Entity not found: ${e.message}", e)
            ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            logger.error("Error occurred while fetching entity with ID: $id", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } finally {
            logger.debug("Exiting get()")
        }
    }

    /**
     * Creates a new entity.
     *
     * @param dto The DTO_IN object containing the data to create the entity.
     * @return A ResponseEntity containing the DTO_OUT of the created entity or an HTTP status code.
     */
    override fun create(dto: DTO_IN): ResponseEntity<DTO_OUT> {
        logger.debug("Entering create() with DTO: {}", dto)
        return try {
            val createdItem = service.create(dto.let(mapper::convertDtoToModel)).let(mapper::convertModelToDtoOut)
            logger.debug("Successfully created entity with ID: {}", createdItem.id)
            ResponseEntity(createdItem, HttpStatus.CREATED)
        } catch (e: EntityValidationException) {
            logger.warn("Validation failed during entity creation: ${e.message}", e)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            logger.error("Error occurred while creating entity", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } finally {
            logger.debug("Exiting create()")
        }
    }

    /**
     * Updates an existing entity by its ID.
     *
     * @param id The ID of the entity to update.
     * @param dto The DTO_IN object containing the updated data.
     * @return A ResponseEntity containing the DTO_OUT of the updated entity or an HTTP status code.
     */
    override fun update(id: ID, dto: DTO_IN): ResponseEntity<DTO_OUT> {
        logger.debug("Entering update() with ID: {} and DTO: {}", id, dto)
        return try {
            if (id != dto.id) {
                throw IllegalArgumentException("Path variable ID and DTO ID do not match.")
            }
            val updatedItem = service.update(dto.let(mapper::convertDtoToModel)).let(mapper::convertModelToDtoOut)
            logger.debug("Successfully updated entity with ID: {}", updatedItem.id)
            ResponseEntity(updatedItem, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            logger.warn("Update failed: Path variable ID and DTO ID do not match", e)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (e: EntityNotFoundException) {
            logger.warn("Entity not found during update: ${e.message}", e)
            ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (e: EntityValidationException) {
            logger.warn("Validation failed during entity update: ${e.message}", e)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            logger.error("Error occurred while updating entity with ID: $id", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } finally {
            logger.debug("Exiting update()")
        }
    }

    /**
     * Deletes a specific entity by its ID.
     *
     * @param id The ID of the entity to delete.
     * @return A ResponseEntity with an HTTP status code.
     */
    override fun delete(id: ID): ResponseEntity<Unit> {
        logger.debug("Entering delete() with ID: {}", id)
        return try {
            service.deleteById(id)
            logger.debug("Successfully deleted entity with ID: {}", id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: EntityNotFoundException) {
            logger.warn("Entity not found during deletion: ${e.message}", e)
            ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            logger.error("Error occurred while deleting entity with ID: $id", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } finally {
            logger.debug("Exiting delete()")
        }
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
        return try {
            service.deleteAll()
            logger.warn("CRITICAL OPERATION COMPLETED: Successfully deleted all entities")
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: Exception) {
            logger.error("Error occurred while deleting all entities", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } finally {
            logger.debug("Exiting deleteAll()")
        }
    }

    /**
     * Adds a list of entities.
     *
     * @param dtos The list of DTO_IN objects to add.
     * @return A ResponseEntity with the created entities or an HTTP status code.
     */
    override fun addAll(dtos: List<DTO_IN>): ResponseEntity<List<DTO_OUT>> {
        logger.debug("Entering addAll() with DTOs count: {}", dtos.size)
        return try {
            val createdItems = service.addAll(dtos.map(mapper::convertDtoToModel))
                .map(mapper::convertModelToDtoOut)
            logger.debug("Successfully added {} entities", createdItems.size)
            ResponseEntity(createdItems, HttpStatus.CREATED)
        } catch (e: EntityValidationException) {
            logger.warn("Validation failed during batch addition: ${e.message}", e)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            logger.error("Error occurred while adding entities", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } finally {
            logger.debug("Exiting addAll()")
        }
    }

    /**
     * Updates a list of entities.
     *
     * @param dtos The list of DTO_IN objects containing the updated data.
     * @return A ResponseEntity with the updated entities or an HTTP status code.
     */
    override fun updateAll(dtos: List<DTO_IN>): ResponseEntity<List<DTO_OUT>> {
        logger.debug("Entering updateAll() with DTOs count: {}", dtos.size)
        return try {
            val updatedItems = service.updateAll(dtos.map(mapper::convertDtoToModel))
                .map(mapper::convertModelToDtoOut)
            logger.debug("Successfully updated {} entities", updatedItems.size)
            ResponseEntity(updatedItems, HttpStatus.OK)
        } catch (e: EntityNotFoundException) {
            logger.warn("One or more entities not found during batch update: ${e.message}", e)
            ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (e: EntityValidationException) {
            logger.warn("Validation failed during batch update: ${e.message}", e)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            logger.error("Error occurred while updating entities", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } finally {
            logger.debug("Exiting updateAll()")
        }
    }

    /**
     * Extension point for adding security annotations in implementing controllers.
     * Override this method to apply security restrictions.
     */
    protected open fun checkSecurity(operation: String, entityId: ID? = null) {
        // No security by default - to be implemented in concrete controllers
    }
}