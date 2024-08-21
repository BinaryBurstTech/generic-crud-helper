package cz.binaryburst.generic.dto

import java.io.Serializable

/**
 * Abstract base class for data transfer objects (DTOs) used for inserting new entities.
 *
 * @param ID The type of the entity identifier.
 */
abstract class BaseInsertDto<ID : Serializable> {
    /**
     * The identifier of the entity.
     */
    abstract var id: ID
}
