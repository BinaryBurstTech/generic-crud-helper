package cz.binaryburst.generic.service

import cz.binaryburst.generic.dto.OrderableDtoInput
import cz.binaryburst.generic.dto.OrderableDtoOutput
import cz.binaryburst.generic.entity.OrderableEntity
import cz.binaryburst.generic.exception.EntityNotFoundException
import cz.binaryburst.generic.mapper.IOrderableMapper
import cz.binaryburst.generic.model.OrderableModel
import cz.binaryburst.generic.repository.IOrderablePositionableRepository
import cz.binaryburst.generic.repository.OrderableRepository
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
), IOrderableService<ID, PARAMS, MODEL> {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Retrieves all entities sorted by position.
     */
    @Transactional
    override fun findAllOrderable(params: PARAMS?): List<MODEL> {
        logger.debug("Entering findAll()")
        return try {
            val models =
                positionableRepository.findAllByOrderByPositionAsc(params).map { mapper.convertEntityToModel(it) }
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
    override fun createOrderable(model: MODEL, params: PARAMS?): MODEL {
        logger.debug("Entering create() with model: {}", model)
        return try {
            val position = model.position ?: positionableRepository.findMaxPosition(params)?.plus(1) ?: 1
            model.position = position
            positionableRepository.incrementPositions(position, params)
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
    override fun deleteOrderableById(id: ID, params: PARAMS?) {
        logger.debug("Entering deleteById() with ID: {}", id)
        try {
            val entity = repository.findByIdOrNull(id) ?: throw EntityNotFoundException(id, "Entity")
            super.deleteById(id)
            positionableRepository.decrementPositions(entity.position, params)
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
    override fun addAllOrderable(models: List<MODEL>, params: PARAMS?): List<MODEL> {
        logger.debug("Entering addAll() with models: {}", models)
        return try {
            models.map { createOrderable(it, params) }
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
    override fun reorder(model: MODEL, params: PARAMS?) {
        logger.debug("Entering reorder() with model: {}", model)
        try {
            val (entityToUpdate, newPosition) = getEntityAndUpdatePosition(model, params)
            if (entityToUpdate.position != newPosition) {
                if (newPosition < entityToUpdate.position) {
                    positionableRepository.incrementPositions(newPosition, entityToUpdate.position, params)
                } else {
                    positionableRepository.decrementPositions(entityToUpdate.position, newPosition, params)
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
     * Delete multiple entities.
     */
    @Transactional
    override fun deleteOrderableAll(params: PARAMS?) {
        logger.debug("Entering deleteAll()")
        try {
            positionableRepository.deleteAllByParams(params)
            logger.debug("Successfully deleted all entities")
        } catch (e: Exception) {
            logger.error("Error occurred while deleting all entities", e)
            throw e
        } finally {
            logger.debug("Exiting deleteAll()")
        }
    }

    /**
     * Helper function to retrieve an entity and update its position.
     */
    private fun getEntityAndUpdatePosition(model: MODEL, params: PARAMS?): Pair<ENTITY, Int> {
        val entityId = model.id
        val entityToUpdate = repository.findByIdOrNull(entityId)
            ?: throw EntityNotFoundException(entityId, "Entity")
        val maxPosition = positionableRepository.findMaxPosition(params) ?: 1
        val newPosition = model.position?.coerceIn(1, maxPosition + 1) ?: (maxPosition + 1)
        return Pair(entityToUpdate, newPosition)
    }
}