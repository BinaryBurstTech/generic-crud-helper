package cz.binaryburst.generic.service

import cz.binaryburst.generic.dto.BaseDtoInput
import cz.binaryburst.generic.dto.BaseDtoOutput
import cz.binaryburst.generic.dto.PageResponse
import cz.binaryburst.generic.entity.BaseEntity
import cz.binaryburst.generic.exception.EntityIdAlreadyExistException
import cz.binaryburst.generic.exception.EntityIdNotFoundException
import cz.binaryburst.generic.exception.EntityNotFoundException
import cz.binaryburst.generic.mapper.IBaseMapper
import cz.binaryburst.generic.model.BaseModel
import cz.binaryburst.generic.repository.BaseRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

/**
 * Base service class providing common CRUD operations for entities.
 *
 * @param ID The type of the entity identifier.
 * @param DTO_IN The type of the input DTO.
 * @param DTO_OUT The type of the output DTO.
 * @param MODEL The type of the model representing the business logic.
 * @param ENTITY The type of the entity managed by the repository.
 * @param REPO The type of the repository managing the entity.
 * @param MAPPER The type of the data mapper used for converting between models, entities, and DTOs.
 */
abstract class BaseService<
        ID : Serializable,
        DTO_IN : BaseDtoInput<ID>,
        DTO_OUT : BaseDtoOutput<ID>,
        MODEL : BaseModel<ID>,
        ENTITY : BaseEntity<ID>,
        REPO : BaseRepository<ENTITY, ID>,
        MAPPER : IBaseMapper<DTO_IN, DTO_OUT, MODEL, ENTITY, ID>>(
    open val repository: REPO,
    open val mapper: MAPPER
) : IBaseService<ID, MODEL> {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Retrieves entities with pagination support.
     *
     * @param pageable Pagination information.
     * @return A paginated response of models.
     */
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): PageResponse<MODEL> {
        logger.debug("Entering findAll() with pageable: {}", pageable)

        val page = repository.findAllBy(pageable)
        val models = page.content.map { mapper.convertEntityToModel(it) }

        logger.debug("Successfully retrieved {} entities", models.size)
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
     * Retrieves all entities (deprecated, use findAll with pagination instead).
     *
     * @return A list of all models.
     */
    @Deprecated(
        "Use findAll(pageable) instead for better performance with large datasets",
        ReplaceWith("findAll(Pageable.unpaged())")
    )
    @Transactional(readOnly = true)
    override fun findAll(): List<MODEL> {
        logger.debug("Entering findAll() - DEPRECATED METHOD")

        val entities = repository.findAll()
        val models = entities.map { mapper.convertEntityToModel(it) }

        logger.debug("Successfully retrieved ${models.size} entities")
        return models
    }

    /**
     * Creates a new entity in the repository.
     *
     * @param model The model representing the entity to be created.
     * @return The created model.
     * @throws EntityIdAlreadyExistException if an entity with the same ID already exists.
     */
    @Transactional
    override fun create(model: MODEL): MODEL {
        logger.debug("Entering create() with model: {}", model)

        model.getId()?.let { validateNewEntityId(it) }

        val entity = model.toEntity()
        val savedEntity = repository.save(entity)
        val savedModel = mapper.convertEntityToModel(savedEntity)

        logger.debug("Successfully created entity with ID: {}", savedModel.id)
        return savedModel
    }

    /**
     * Finds an entity by its ID.
     *
     * @param id The ID of the entity to find.
     * @return The model of the found entity.
     * @throws EntityNotFoundException if no entity with the given ID is found.
     */
    @Transactional(readOnly = true)
    override fun findById(id: ID): MODEL {
        logger.debug("Entering findById() with ID: {}", id)

        val entity = repository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(id, "Entity")

        val model = mapper.convertEntityToModel(entity)
        logger.debug("Successfully found entity with ID: {}", id)

        return model
    }

    /**
     * Checks if an entity with the given ID exists.
     *
     * @param id The ID to check.
     * @return True if an entity with the given ID exists, false otherwise.
     */
    @Transactional(readOnly = true)
    override fun existsById(id: ID): Boolean {
        logger.debug("Checking if entity exists with ID: {}", id)
        return repository.existsWithId(id)
    }

    /**
     * Updates an existing entity in the repository.
     *
     * @param model The model representing the updated entity.
     * @return The updated model.
     * @throws EntityNotFoundException if no entity with the given ID is found.
     * @throws EntityIdNotFoundException if the entity ID is not provided.
     */
    @Transactional
    override fun update(model: MODEL): MODEL {
        logger.debug("Entering update() with model: {}", model)

        val entityId = model.getId() ?: throw EntityIdNotFoundException("update")

        val entity = repository.findByIdOrNull(entityId)
            ?: throw EntityNotFoundException(entityId, "Entity")

        val updatedEntity = mapper.updateEntityFromModel(entity, model)
        val savedEntity = repository.save(updatedEntity)
        val updatedModel = mapper.convertEntityToModel(savedEntity)

        logger.debug("Successfully updated entity with ID: {}", updatedModel.id)
        return updatedModel
    }

    /**
     * Deletes an entity by its ID.
     *
     * @param id The ID of the entity to delete.
     * @throws EntityNotFoundException if no entity with the given ID is found.
     */
    @Transactional
    override fun deleteById(id: ID) {
        logger.debug("Entering deleteById() with ID: {}", id)

        if (!repository.existsWithId(id)) {
            throw EntityNotFoundException(id, "Entity")
        }

        repository.deleteById(id)
        logger.debug("Successfully deleted entity with ID: {}", id)
    }

    /**
     * Adds multiple entities to the repository.
     *
     * @param models A list of models representing the entities to add.
     * @return A list of the added models.
     * @throws EntityIdAlreadyExistException if any entity with the same ID already exists.
     */
    @Transactional
    override fun addAll(models: List<MODEL>): List<MODEL> {
        logger.debug("Entering addAll() with models: {}", models)

        // Check IDs for all entities
        models.forEach { model ->
            model.getId()?.let { validateNewEntityId(it) }
        }

        val entities = models.map { it.toEntity() }
        val savedEntities = repository.saveAllAndFlush(entities)
        val savedModels = savedEntities.map { mapper.convertEntityToModel(it) }

        logger.debug("Successfully added ${savedModels.size} entities")
        return savedModels
    }

    /**
     * Updates multiple entities in the repository.
     *
     * @param models A list of models representing the entities to update.
     * @return A list of the updated models.
     * @throws EntityNotFoundException if any entity is not found.
     * @throws EntityIdNotFoundException if any entity ID is not provided.
     */
    @Transactional
    override fun updateAll(models: List<MODEL>): List<MODEL> {
        logger.debug("Entering updateAll() with models: {}", models)

        val updatedEntities = models.map { model ->
            val entityId = model.getId() ?: throw EntityIdNotFoundException("updateAll")
            val entity = repository.findByIdOrNull(entityId)
                ?: throw EntityNotFoundException(entityId, "Entity")
            mapper.updateEntityFromModel(entity, model)
        }

        val savedEntities = repository.saveAllAndFlush(updatedEntities)
        val updatedModels = savedEntities.map { mapper.convertEntityToModel(it) }

        logger.debug("Successfully updated ${updatedModels.size} entities")
        return updatedModels
    }

    /**
     * Deletes all entities in the repository.
     *
     * Note: This operation should be used with extreme caution as it removes all data.
     */
    @Deprecated("This method poses a significant risk to data integrity. Use with extreme caution.")
    @Transactional
    override fun deleteAll() {
        logger.warn("CRITICAL OPERATION: Entering deleteAll() - will delete ALL entities")

        repository.deleteAll()

        logger.warn("CRITICAL OPERATION COMPLETED: Successfully deleted all entities")
    }

    /**
     * Converts a model to an entity.
     *
     * @return The entity corresponding to the model.
     */
    private fun MODEL.toEntity(): ENTITY = mapper.convertModelToEntity(this)

    /**
     * Extracts the ID from a model.
     *
     * @return The ID of the model, or null if the model has no ID.
     */
    private fun MODEL.getId(): ID? = mapper.extractIdFromModel(this)

    /**
     * Validates that a new entity ID does not already exist in the repository.
     *
     * @param id The ID to validate.
     * @throws EntityIdAlreadyExistException if an entity with the given ID already exists.
     */
    private fun validateNewEntityId(id: ID) {
        logger.debug("Validating new entity ID: {}", id)

        if (repository.existsWithId(id)) {
            logger.warn("Entity with ID $id already exists")
            throw EntityIdAlreadyExistException(id, "Entity")
        }
    }
}