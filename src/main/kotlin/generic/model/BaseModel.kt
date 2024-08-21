package cz.binaryburst.generic.model

import java.io.Serializable

/**
 * Abstract base class for models representing business logic.
 *
 * @param ID The type of the entity identifier.
 */
abstract class BaseModel<ID : Serializable> {
    /**
     * The identifier of the model.
     */
    abstract val id: ID
}
