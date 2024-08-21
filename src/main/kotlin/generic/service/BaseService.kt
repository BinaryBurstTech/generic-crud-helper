package cz.binaryburst.generic.service

import cz.binaryburst.generic.dto.BaseInsertDto
import cz.binaryburst.generic.dto.BaseOutputDto
import cz.binaryburst.generic.entity.BaseEntity
import cz.binaryburst.generic.exception.EntityIdAlreadyExistException
import cz.binaryburst.generic.exception.EntityIdNotFoundException
import cz.binaryburst.generic.exception.EntityNotFoundException
import cz.binaryburst.generic.exception.EntityValidationException
import cz.binaryburst.generic.mapper.IDataMapper
import cz.binaryburst.generic.model.BaseModel
import cz.binaryburst.generic.repository.BaseRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

abstract class BaseService<
        ID : Serializable,
        DTO_IN : BaseInsertDto<ID>,
        DTO_OUT : BaseOutputDto<ID>,
        MODEL : BaseModel<ID>,
        ENTITY : BaseEntity<ID>,
        REPO : BaseRepository<ENTITY, ID>,
        MAPPER : IDataMapper<DTO_IN, DTO_OUT, MODEL, ENTITY, ID>>(
    open val repository: REPO,
    open val mapper: MAPPER
) : ICrudService<ID, MODEL> {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

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

    private fun MODEL.toEntity(): ENTITY = mapper.convertModelToEntity(this)

    private fun MODEL.getId(): ID? = mapper.extractIdFromModel(this)

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
