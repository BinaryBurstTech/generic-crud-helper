package cz.binaryburst.generic.service

import cz.binaryburst.generic.dto.OrderableDtoInput
import cz.binaryburst.generic.dto.OrderableDtoOutput
import cz.binaryburst.generic.dto.PageResponse
import cz.binaryburst.generic.entity.OrderableEntity
import cz.binaryburst.generic.exception.EntityNotFoundException
import cz.binaryburst.generic.mapper.IOrderableMapper
import cz.binaryburst.generic.model.OrderableModel
import cz.binaryburst.generic.repository.IOrderablePositionableRepository
import cz.binaryburst.generic.repository.OrderableRepository
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
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
     * Retrieves all entities sorted by position with pagination support.
     *
     * @param pageable Pagination information.
     * @param params Optional parameters for filtering.
     * @return A paginated response of models sorted by position.
     */
    @Transactional
    override fun findAllOrderable(pageable: Pageable, params: PARAMS?): PageResponse<MODEL> {
        logger.debug("Entering findAllOrderable() with pageable: {}", pageable)

        val page = positionableRepository.findAllByOrderByPositionAsc(pageable, params)
        val models = page.content.map { mapper.convertEntityToModel(it) }

        logger.debug("Successfully retrieved paginated entities, page size: {}", models.size)

        return PageResponse(
            content = models,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            pageNumber = page.number,
            pageSize = page.size,
            isLast = page.isLast
        )
    }

    /**
     * Retrieves all entities sorted by position.
     *
     * @param params Optional parameters for filtering.
     * @return A list of models sorted by position.
     */
    @Deprecated(
        "Use findAllOrderable(pageable, params) for better performance with large datasets",
        ReplaceWith("findAllOrderable(Pageable.unpaged(), params)")
    )
    @Transactional
    override fun findAllOrderable(params: PARAMS?): List<MODEL> {
        logger.debug("Entering findAllOrderable() - DEPRECATED METHOD")

        val entities = positionableRepository.findAllByOrderByPositionAsc(params)
        val models = entities.map { mapper.convertEntityToModel(it) }

        logger.debug("Successfully retrieved {} orderable entities", models.size)
        return models
    }

    /**
     * Creates a new entity and manages its position.
     *
     * @param model The model representing the entity to create.
     * @param params Optional parameters for context.
     * @return The created model.
     * @throws EntityIdAlreadyExistException if an entity with the same ID already exists.
     */
    @Transactional
    override fun createOrderable(model: MODEL, params: PARAMS?): MODEL {
        logger.debug("Entering createOrderable() with model: {}", model)

        val position = model.position ?: positionableRepository.findMaxPosition(params)?.plus(1) ?: 1
        model.position = position
        positionableRepository.incrementPositions(position, params)

        return super.create(model)
    }

    /**
     * Deletes an entity by ID and updates positions.
     *
     * @param id The ID of the entity to delete.
     * @param params Optional parameters for context.
     * @throws EntityNotFoundException if no entity with the given ID is found.
     */
    @Transactional
    override fun deleteOrderableById(id: ID, params: PARAMS?) {
        logger.debug("Entering deleteOrderableById() with ID: {}", id)

        val entity = repository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(id, "Entity")

        super.deleteById(id)
        positionableRepository.decrementPositions(entity.position, params)

        logger.debug("Successfully deleted orderable entity with ID: {}", id)
    }

    /**
     * Adds multiple entities, ensuring proper position handling.
     *
     * @param models The list of models to add.
     * @param params Optional parameters for context.
     * @return The list of created models.
     * @throws EntityIdAlreadyExistException if any entity with the same ID already exists.
     */
    @Transactional
    override fun addAllOrderable(models: List<MODEL>, params: PARAMS?): List<MODEL> {
        logger.debug("Entering addAllOrderable() with models count: {}", models.size)

        return models.map { createOrderable(it, params) }
    }

    /**
     * Reorders an entity within the list based on its new position.
     *
     * @param model The model containing the new position.
     * @param params Optional parameters for context.
     * @throws EntityNotFoundException if no entity with the given ID is found.
     */
    @Transactional
    override fun reorder(model: MODEL, params: PARAMS?) {
        logger.debug("Entering reorder() with model: {}", model)

        val entityId = model.id
        val entityToUpdate = repository.findByIdOrNull(entityId)
            ?: throw EntityNotFoundException(entityId, "Entity")

        val maxPosition = positionableRepository.findMaxPosition(params) ?: 1
        val newPosition = model.position?.coerceIn(1, maxPosition) ?: maxPosition

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
    }

    /**
     * Delete multiple entities.
     *
     * @param params Optional parameters for context.
     */
    @Deprecated("This method poses a significant risk to data integrity. Use with extreme caution.")
    @Transactional
    override fun deleteOrderableAll(params: PARAMS?) {
        logger.warn("CRITICAL OPERATION: Entering deleteOrderableAll()")

        positionableRepository.deleteAllByParams(params)

        logger.warn("CRITICAL OPERATION COMPLETED: Successfully deleted all orderable entities")
    }
}