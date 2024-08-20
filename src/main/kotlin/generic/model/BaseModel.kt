package cz.binaryburst.generic.model

import java.io.Serializable

abstract class BaseModel<ID : Serializable> {
    abstract val id: ID
}


