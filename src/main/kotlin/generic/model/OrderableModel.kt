package cz.binaryburst.generic.model

import java.io.Serializable

abstract class OrderableModel<ID : Serializable> : BaseModel<ID>() {
    abstract var position: Int?
}