package cz.binaryburst.generic.entity

import java.io.Serializable

/**
 * Abstract base class for all entities.
 *
 * @param ID The type of the entity identifier.
 */
abstract class BaseEntity<ID : Serializable> {
    /**
     * The identifier of the entity.
     */
    abstract val id: ID
}
