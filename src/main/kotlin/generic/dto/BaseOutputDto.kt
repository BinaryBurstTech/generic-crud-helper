package cz.binaryburst.generic.dto

import java.io.Serializable

abstract class BaseOutputDto<ID : Serializable> {
    abstract val id: ID
}
