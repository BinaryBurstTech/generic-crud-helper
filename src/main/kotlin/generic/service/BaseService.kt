package cz.binaryburst.generic.service

import cz.binaryburst.generic.dto.BaseInsertDto
import cz.binaryburst.generic.dto.BaseOutputDto
import cz.binaryburst.generic.entity.BaseEntity
import cz.binaryburst.generic.exception.EntityIdAlreadyExistException
import cz.binaryburst.generic.exception.EntityIdNotFoundException
import cz.binaryburst.generic.exception.EntityNotFoundException
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
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    open fun findAll(): List<MODEL> =
        logDebug("Fetching all entities") {
            repository.findAll().map { mapper.convertEntityToModel(it) }
        }

    @Transactional
    open fun create(model: MODEL): MODEL =
        logDebug("Creating entity: $model") {
            model.getId()?.let { validateNewEntityId(it) }
            return model.toEntity().let(repository::save).let(mapper::convertEntityToModel)
        }

    open fun findById(id: ID): MODEL =
        logDebug("Finding entity by ID: $id") {
            repository.findByIdOrNull(id)?.let(mapper::convertEntityToModel)
                ?: throw EntityNotFoundException(id)
        }

    @Transactional
    open fun update(model: MODEL): MODEL =
        logDebug("Updating entity: $model") {
            val entityId = model.getId() ?: throw EntityIdNotFoundException()
            val entity = repository.findByIdOrNull(entityId) ?: throw EntityNotFoundException(entityId)
            mapper.updateEntityFromModel(entity, model).let(repository::save).let(mapper::convertEntityToModel)
        }

    @Transactional
    open fun deleteById(id: ID) {
        logDebug("Deleting entity by ID: $id") {
            repository.deleteById(id)
        }
    }

    @Transactional
    open fun addAll(models: List<MODEL>): List<MODEL> =
        logDebug("Adding all entities") {
            models.map { it.toEntity() }
                .let(repository::saveAllAndFlush)
                .map { mapper.convertEntityToModel(it) }
        }

    @Transactional
    open fun updateAll(models: List<MODEL>): List<MODEL> =
        logDebug("Updating all entities") {
            models.map { model ->
                val entityId = model.getId() ?: throw EntityIdNotFoundException()
                val entity = repository.findByIdOrNull(entityId) ?: throw EntityNotFoundException(entityId)
                mapper.updateEntityFromModel(entity, model)
            }.let(repository::saveAllAndFlush)
                .map { mapper.convertEntityToModel(it) }
        }

    @Transactional
    open fun deleteAll() {
        logDebug("Deleting all entities") {
            repository.deleteAll()
        }
    }

    private fun MODEL.toEntity(): ENTITY = mapper.convertModelToEntity(this)
    private fun MODEL.getId(): ID? = mapper.extractIdFromModel(this)

    private fun validateNewEntityId(id: ID?) {
        id?.let { validateId ->
            repository.findById(validateId).ifPresent {
                throw EntityIdAlreadyExistException("Entity with ID $it already exists.")
            }
        }
    }

    private inline fun <T> logDebug(message: String, action: () -> T): T {
        logger.debug(message)
        return action()
    }
}