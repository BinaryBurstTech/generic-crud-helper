package cz.binaryburst.generic.exception

import java.io.Serializable

/**
 * Exception thrown when an entity with the specified identifier is not found.
 *
 * @param id The identifier of the entity that was not found.
 * @param entityType The type of the entity that was not found.
 */
class EntityNotFoundException(val id: Serializable, val entityType: String) :
    RuntimeException("Entity of type '$entityType' not found with id '$id'")

/**
 * Exception thrown when an entity's identifier is required but not provided.
 *
 * @param operation The operation that required the entity ID.
 */
class EntityIdNotFoundException(val operation: String) :
    RuntimeException("Entity ID is required for the operation '$operation'")

/**
 * Exception thrown when an entity with the specified identifier already exists.
 *
 * @param id The identifier of the entity that already exists.
 * @param entityType The type of the entity that already exists.
 */
class EntityIdAlreadyExistException(val id: Serializable, val entityType: String) :
    RuntimeException("Entity of type '$entityType' already exists with id '$id'")

/**
 * Exception thrown when validation fails for an entity.
 *
 * @param message The validation error message.
 * @param errors Optional map containing detailed validation errors.
 */
class EntityValidationException(
    override val message: String,
    val errors: Map<String, String> = emptyMap()
) : RuntimeException("Entity validation failed: $message")