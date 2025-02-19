package cz.binaryburst.generic.dto

import java.io.Serializable

abstract class OrderableDtoOutput<ID : Serializable> : BaseDtoOutput<ID>() {
    abstract var position: Int
}