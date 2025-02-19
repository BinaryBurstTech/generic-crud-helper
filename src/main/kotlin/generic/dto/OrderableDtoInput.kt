package cz.binaryburst.generic.dto

import java.io.Serializable

abstract class OrderableDtoInput<ID : Serializable> : BaseDtoInput<ID>() {
    abstract var position: Int?
}