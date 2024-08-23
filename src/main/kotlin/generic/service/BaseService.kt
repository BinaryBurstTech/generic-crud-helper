package cz.binaryburst.generic.service

import cz.binaryburst.generic.dto.BaseDtoInput
import cz.binaryburst.generic.dto.BaseDtoOutput
import cz.binaryburst.generic.entity.BaseEntity
import cz.binaryburst.generic.exception.EntityIdAlreadyExistException
import cz.binaryburst.generic.exception.EntityIdNotFoundException
import cz.binaryburst.generic.exception.EntityNotFoundException
import cz.binaryburst.generic.exception.EntityValidationException
import cz.binaryburst.generic.mapper.IBaseMapper
import cz.binaryburst.generic.model.BaseModel
import cz.binaryburst.generic.repository.BaseRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
     * Retrieves all entities from the repository.
     *
     * @return A list of all models.
     * @throws Exception if an error occurs while fetching the entities.
     */
    override fun findAll(): List<MODEL> {
        logger.debug("Entering findAll()")
        return try {
            val models = repository.findAll().map { mapper.convertEntityToModel(it) }
            logger.debug("Successfully retrieved ${models.size} entities")
            models
        } catch (e: Exception) {
            logger.error("Error occurred while fetching all entities", e)
            throw e
        } finally {
            logger.debug("Exiting findAll()")
        }
    }

    /**
     * Creates a new entity in the repository.
     *
     * @param model The model representing the entity to be created.
     * @return The created model.
     * @throws EntityIdAlreadyExistException if an entity with the same ID already exists.
     * @throws EntityValidationException if the entity validation fails.
     * @throws Exception if an error occurs during creation.
     */
    @Transactional
    override fun create(model: MODEL): MODEL {
        logger.debug("Entering create() with model: {}", model)
        return try {
            model.getId()?.let { validateNewEntityId(it) }
            val entity = model.toEntity()
            val savedEntity = repository.save(entity)
            val savedModel = mapper.convertEntityToModel(savedEntity)
            logger.debug("Successfully created entity with ID: {}", savedModel.id)
            savedModel
        } catch (e: EntityIdAlreadyExistException) {
            logger.warn("Entity creation failed: ID already exists", e)
            throw e
        } catch (e: EntityValidationException) {
            logger.warn("Entity creation failed: Validation error", e)
            throw e
        } catch (e: Exception) {
            logger.error("Error occurred while creating entity", e)
            throw e
        } finally {
            logger.debug("Exiting create()")
        }
    }

    /**
     * Finds an entity by its ID.
     *
     * @param id The ID of the entity to find.
     * @return The model of the found entity.
     * @throws EntityNotFoundException if no entity with the given ID is found.
     * @throws Exception if an error occurs during the search.
     */
    override fun findById(id: ID): MODEL {
        logger.debug("Entering findById() with ID: {}", id)
        return try {
            val model = repository.findByIdOrNull(id)?.let(mapper::convertEntityToModel)
                ?: throw EntityNotFoundException(id, "Entity")
            logger.debug("Successfully found entity with ID: {}", id)
            model
        } catch (e: EntityNotFoundException) {
            logger.warn("Entity not found: $e.message", e)
            throw e
        } catch (e: Exception) {
            logger.error("Error occurred while finding entity by ID: $id", e)
            throw e
        } finally {
            logger.debug("Exiting findById()")
        }
    }

    /**
     * Updates an existing entity in the repository.
     *
     * @param model The model representing the updated entity.
     * @return The updated model.
     * @throws EntityNotFoundException if no entity with the given ID is found.
     * @throws EntityIdNotFoundException if the entity ID is not provided.
     * @throws EntityValidationException if the entity validation fails.
     * @throws Exception if an error occurs during the update.
     */
    @Transactional
    override fun update(model: MODEL): MODEL {
        logger.debug("Entering update() with model: {}", model)
        return try {
            val entityId = model.getId() ?: throw EntityIdNotFoundException("update")
            val entity = repository.findByIdOrNull(entityId) ?: throw EntityNotFoundException(entityId, "Entity")
            mapper.updateEntityFromModel(entity, model)
            val updatedEntity = repository.save(entity)
            val updatedModel = mapper.convertEntityToModel(updatedEntity)
            logger.debug("Successfully updated entity with ID: {}", updatedModel.id)
            updatedModel
        } catch (e: EntityNotFoundException) {
            logger.warn("Update failed: Entity not found", e)
            throw e
        } catch (e: EntityIdNotFoundException) {
            logger.warn("Update failed: Entity ID not provided", e)
            throw e
        } catch (e: EntityValidationException) {
            logger.warn("Update failed: Validation error", e)
            throw e
        } catch (e: Exception) {
            logger.error("Error occurred while updating entity", e)
            throw e
        } finally {
            logger.debug("Exiting update()")
        }
    }

    /**
     * Deletes an entity by its ID.
     *
     * @param id The ID of the entity to delete.
     * @throws Exception if an error occurs during deletion.
     */
    @Transactional
    override fun deleteById(id: ID) {
        logger.debug("Entering deleteById() with ID: {}", id)
        try {
            repository.deleteById(id)
            logger.debug("Successfully deleted entity with ID: {}", id)
        } catch (e: Exception) {
            logger.error("Error occurred while deleting entity by ID: $id", e)
            throw e
        } finally {
            logger.debug("Exiting deleteById()")
        }
    }

    /**
     * Adds multiple entities to the repository.
     *
     * @param models A list of models representing the entities to add.
     * @return A list of the added models.
     * @throws EntityValidationException if any entity validation fails.
     * @throws Exception if an error occurs during the addition.
     */
    @Transactional
    override fun addAll(models: List<MODEL>): List<MODEL> {
        logger.debug("Entering addAll() with models: {}", models)
        return try {
            val entities = models.map { it.toEntity() }
            val savedEntities = repository.saveAllAndFlush(entities)
            val savedModels = savedEntities.map { mapper.convertEntityToModel(it) }
            logger.debug("Successfully added ${savedModels.size} entities")
            savedModels
        } catch (e: EntityValidationException) {
            logger.warn("Batch addition failed: Validation error", e)
            throw e
        } catch (e: Exception) {
            logger.error("Error occurred while adding entities", e)
            throw e
        } finally {
            logger.debug("Exiting addAll()")
        }
    }

    /**
     * Updates multiple entities in the repository.
     *
     * @param models A list of models representing the entities to update.
     * @return A list of the updated models.
     * @throws EntityNotFoundException if any entity is not found.
     * @throws EntityIdNotFoundException if any entity ID is not provided.
     * @throws EntityValidationException if any entity validation fails.
     * @throws Exception if an error occurs during the update.
     */
    @Transactional
    override fun updateAll(models: List<MODEL>): List<MODEL> {
        logger.debug("Entering updateAll() with models: {}", models)
        return try {
            val updatedEntities = models.map { model ->
                val entityId = model.getId() ?: throw EntityIdNotFoundException("updateAll")
                val entity = repository.findByIdOrNull(entityId) ?: throw EntityNotFoundException(entityId, "Entity")
                mapper.updateEntityFromModel(entity, model)
            }
            val savedEntities = repository.saveAllAndFlush(updatedEntities)
            val updatedModels = savedEntities.map { mapper.convertEntityToModel(it) }
            logger.debug("Successfully updated ${updatedModels.size} entities")
            updatedModels
        } catch (e: EntityNotFoundException) {
            logger.warn("Batch update failed: Some entities not found", e)
            throw e
        } catch (e: EntityIdNotFoundException) {
            logger.warn("Batch update failed: Some entities lacked an ID", e)
            throw e
        } catch (e: EntityValidationException) {
            logger.warn("Batch update failed: Validation error", e)
            throw e
        } catch (e: Exception) {
            logger.error("Error occurred while updating entities", e)
            throw e
        } finally {
            logger.debug("Exiting updateAll()")
        }
    }

    /**
     * Deletes all entities in the repository.
     *
     * @throws Exception if an error occurs during deletion.
     */
    @Transactional
    override fun deleteAll() {
        logger.debug("Entering deleteAll()")
        try {
            repository.deleteAll()
            logger.debug("Successfully deleted all entities")
        } catch (e: Exception) {
            logger.error("Error occurred while deleting all entities", e)
            throw e
        } finally {
            logger.debug("Exiting deleteAll()")
        }
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
    private fun validateNewEntityId(id: ID?) {
        id?.let { validateId ->
            logger.debug("Validating new entity ID: {}", validateId)
            repository.findById(validateId).ifPresent {
                logger.warn("Entity with ID $validateId already exists")
                throw EntityIdAlreadyExistException(validateId, "Entity")
            }
        }
    }
}
