package cz.binaryburst.generic.controller

import cz.binaryburst.generic.dto.BaseInsertDto
import cz.binaryburst.generic.dto.BaseOutputDto
import cz.binaryburst.generic.entity.BaseEntity
import cz.binaryburst.generic.exception.EntityNotFoundException
import cz.binaryburst.generic.exception.EntityValidationException
import cz.binaryburst.generic.mapper.IDataMapper
import cz.binaryburst.generic.model.BaseModel
import cz.binaryburst.generic.repository.BaseRepository
import cz.binaryburst.generic.service.BaseService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
        DTO_IN : BaseInsertDto<ID>,
        DTO_OUT : BaseOutputDto<ID>,
        MODEL : BaseModel<ID>,
        ENTITY : BaseEntity<ID>,
        MAPPER : IDataMapper<DTO_IN, DTO_OUT, MODEL, ENTITY, ID>,
        REPO : BaseRepository<ENTITY, ID>,
        SERVICE : BaseService<ID, DTO_IN, DTO_OUT, MODEL, ENTITY, REPO, MAPPER>,
        >(
    private val service: SERVICE,
    private val mapper: MAPPER
) : ICrudController<ID, DTO_IN, DTO_OUT> {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Retrieves all entities.
     *
     * @return A ResponseEntity containing a list of all DTO_OUT or an HTTP status code.
     */
    override fun getAll(): ResponseEntity<List<DTO_OUT>> {
        logger.debug("Entering getAll()")
        return try {
            val items = service.findAll().map(mapper::convertModelToDtoOut)
            logger.debug("Successfully retrieved ${items.size} items")
            ResponseEntity(items, HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Error occurred while fetching all entities", e)
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
            check(id != dto.id) {
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
     * @return A ResponseEntity with an HTTP status code.
     */
    override fun deleteAll(): ResponseEntity<Unit> {
        logger.debug("Entering deleteAll()")
        return try {
            service.deleteAll()
            logger.debug("Successfully deleted all entities")
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
     * @return A ResponseEntity with an HTTP status code.
     */
    override fun addAll(dtos: List<DTO_IN>): ResponseEntity<List<DTO_OUT>> {
        logger.debug("Entering addAll() with DTOs: {}", dtos)
        return try {
            service.addAll(dtos.map(mapper::convertDtoToModel))
            logger.debug("Successfully added all entities")
            ResponseEntity(HttpStatus.NO_CONTENT)
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
     * @return A ResponseEntity with an HTTP status code.
     */
    override fun updateAll(dtos: List<DTO_IN>): ResponseEntity<List<DTO_OUT>> {
        logger.debug("Entering updateAll() with DTOs: {}", dtos)
        return try {
            service.updateAll(dtos.map(mapper::convertDtoToModel))
            logger.debug("Successfully updated all entities")
            ResponseEntity(HttpStatus.NO_CONTENT)
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
}
