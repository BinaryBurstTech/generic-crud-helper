package cz.binaryburst.generic.exception

import java.io.Serializable

// ResourceNotFoundException.kt
class EntityNotFoundException(id: Serializable) : RuntimeException("Entity not found with id $id")
class EntityIdNotFoundException : RuntimeException("Entity id is required for update.")
class EntityIdAlreadyExistException(id: Serializable) : RuntimeException("Entity exists with this id: $id")