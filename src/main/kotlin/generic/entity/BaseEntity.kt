package cz.binaryburst.generic.entity

import java.io.Serializable

abstract class BaseEntity<ID : Serializable> {
    abstract val id: ID
}