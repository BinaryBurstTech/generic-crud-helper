package cz.binaryburst.generic.service

import cz.binaryburst.generic.dto.OrderableDtoInput
import cz.binaryburst.generic.dto.OrderableDtoOutput
import cz.binaryburst.generic.entity.OrderableEntity
import cz.binaryburst.generic.exception.EntityNotFoundException
import cz.binaryburst.generic.mapper.IOrderableMapper
import cz.binaryburst.generic.model.OrderableModel
import cz.binaryburst.generic.repository.OrderableRepository
import cz.binaryburst.generic.repository.IOrderablePositionableRepository
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import java.io.Serializable

/**
 * Service class providing CRUD operations with ordering support for entities.
 *
 * @param ID The type of the entity identifier.
 * @param DTO_IN The type of the input DTO.
 * @param DTO_OUT The type of the output DTO.
 * @param MODEL The type of the business logic model.
 * @param ENTITY The type of the entity.
 * @param REPO The repository managing the entity.
 * @param MAPPER The data mapper converting between models, entities, and DTOs.
 */
abstract class OrderableService<
        ID : Serializable,
        PARAMS : OrderableParams,
        DTO_IN : OrderableDtoInput<ID>,
        DTO_OUT : OrderableDtoOutput<ID>,
        MODEL : OrderableModel<ID>,
        ENTITY : OrderableEntity<ID>,
        REPO : OrderableRepository<ENTITY, ID>,
        MAPPER : IOrderableMapper<DTO_IN, DTO_OUT, MODEL, ENTITY, ID>>(
    override val repository: REPO,
    override val mapper: MAPPER,
    val positionableRepository: IOrderablePositionableRepository<PARAMS, ENTITY>
) : BaseService<ID, DTO_IN, DTO_OUT, MODEL, ENTITY, REPO, MAPPER>(
    repository = repository,
    mapper = mapper
), IOrderableService<ID, MODEL> {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Retrieves all entities sorted by position.
     */
    @Transactional
    override fun findAll(): List<MODEL> {
        logger.debug("Entering findAll()")
        return try {
            val models = positionableRepository.findAllByOrderByPositionAsc(null).map { mapper.convertEntityToModel(it) }
            logger.debug("Successfully retrieved {} entities", models.size)
            models
        } catch (e: Exception) {
            logger.error("Error occurred while fetching all entities", e)
            throw e
        } finally {
            logger.debug("Exiting findAll()")
        }
    }

    /**
     * Creates a new entity and manages its position.
     */
    @Transactional
    override fun create(model: MODEL): MODEL {
        logger.debug("Entering create() with model: {}", model)
        return try {
            val position = model.position ?: positionableRepository.findMaxPosition(null)?.plus(1) ?: 1
            model.position = position
            positionableRepository.incrementPositions(position, null)
            super.create(model)
        } catch (e: Exception) {
            logger.error("Error occurred while creating entity", e)
            throw e
        } finally {
            logger.debug("Exiting create()")
        }
    }

    /**
     * Deletes an entity by ID and updates positions.
     */
    @Transactional
    override fun deleteById(id: ID) {
        logger.debug("Entering deleteById() with ID: {}", id)
        try {
            val entity = repository.findByIdOrNull(id) ?: throw EntityNotFoundException(id, "Entity")
            super.deleteById(id)
            positionableRepository.decrementPositions(entity.position, null)
            logger.debug("Successfully deleted entity with ID: {}", id)
        } catch (e: EntityNotFoundException) {
            logger.warn("Deletion failed: Entity not found", e)
            throw e
        } catch (e: Exception) {
            logger.error("Error occurred while deleting entity", e)
            throw e
        } finally {
            logger.debug("Exiting deleteById()")
        }
    }

    /**
     * Adds multiple entities, ensuring proper position handling.
     */
    @Transactional
    override fun addAll(models: List<MODEL>): List<MODEL> {
        logger.debug("Entering addAll() with models: {}", models)
        return try {
            models.map { create(it) }
        } catch (e: Exception) {
            logger.error("Error occurred while adding entities", e)
            throw e
        } finally {
            logger.debug("Exiting addAll()")
        }
    }

    /**
     * Reorders an entity within the list based on its new position.
     */
    @Transactional
    override fun reorder(model: MODEL) {
        logger.debug("Entering reorder() with model: {}", model)
        try {
            val (entityToUpdate, newPosition) = getEntityAndUpdatePosition(model)
            if (entityToUpdate.position != newPosition) {
                if (newPosition < entityToUpdate.position) {
                    positionableRepository.incrementPositions(newPosition, entityToUpdate.position, null)
                } else {
                    positionableRepository.decrementPositions(entityToUpdate.position, newPosition, null)
                }
                entityToUpdate.position = newPosition
                repository.save(entityToUpdate)
            }
            logger.debug("Successfully reordered entity to position: {}", newPosition)
        } catch (e: EntityNotFoundException) {
            logger.warn("Reorder failed: Entity not found", e)
            throw e
        } catch (e: Exception) {
            logger.error("Error occurred while reordering entity", e)
            throw e
        } finally {
            logger.debug("Exiting reorder()")
        }
    }

    /**
     * Helper function to retrieve an entity and update its position.
     */
    private fun getEntityAndUpdatePosition(model: MODEL): Pair<ENTITY, Int> {
        val entityId = model.id
        val entityToUpdate = repository.findByIdOrNull(entityId)
            ?: throw EntityNotFoundException(entityId, "Entity")
        val maxPosition = positionableRepository.findMaxPosition(null) ?: 1
        val newPosition = model.position?.coerceIn(1, maxPosition + 1) ?: (maxPosition + 1)
        return Pair(entityToUpdate, newPosition)
    }
}