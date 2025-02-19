package cz.binaryburst.generic.entity

import java.io.Serializable

abstract class OrderableEntity<ID : Serializable> : BaseEntity<ID>() {
    abstract var position: Int
}