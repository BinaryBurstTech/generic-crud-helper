package cz.binaryburst.generic.dto

import java.io.Serializable

abstract class BaseInsertDto<ID : Serializable> {
    abstract var id: ID
}

