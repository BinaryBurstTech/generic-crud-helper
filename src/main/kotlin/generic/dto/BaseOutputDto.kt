package cz.binaryburst.generic.dto

import java.io.Serializable

/**
 * Abstract base class for data transfer objects (DTOs) used for outputting entities.
 *
 * @param ID The type of the entity identifier.
 */
abstract class BaseOutputDto<ID : Serializable> {
    /**
     * The identifier of the entity.
     */
    abstract val id: ID
}
