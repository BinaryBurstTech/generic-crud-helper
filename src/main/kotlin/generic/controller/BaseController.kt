package cz.binaryburst.generic.controller

import cz.binaryburst.generic.dto.BaseInsertDto
import cz.binaryburst.generic.dto.BaseOutputDto
import cz.binaryburst.generic.entity.BaseEntity
import cz.binaryburst.generic.mapper.IDataMapper
import cz.binaryburst.generic.model.BaseModel
import cz.binaryburst.generic.repository.BaseRepository
import cz.binaryburst.generic.service.BaseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.Serializable

abstract class BaseController<
        ID : Serializable,
        DTO_IN : BaseInsertDto<ID>,
        DTO_OUT : BaseOutputDto<ID>,
        MODEL : BaseModel<ID>,
        ENTITY : BaseEntity<ID>,
        MAPPER : IDataMapper<DTO_IN, DTO_OUT, MODEL, ENTITY, ID>,
        REPO : BaseRepository<ENTITY, ID>,
        SERVICE : BaseService<ID, DTO_IN, DTO_OUT, MODEL, ENTITY, REPO, MAPPER>,
        >(
    private val service: SERVICE,
    private val mapper: MAPPER
) {

    open fun getAll(): ResponseEntity<List<DTO_OUT>> {
        return try {
            val items = service.findAll().map(mapper::convertModelToDtoOut)
            ResponseEntity(items, HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    open fun get(id: ID): ResponseEntity<DTO_OUT> {
        return ResponseEntity(service.findById(id).let(mapper::convertModelToDtoOut), HttpStatus.OK)
    }

    open fun create(dto: DTO_IN): ResponseEntity<DTO_OUT> {
        return try {
            val createdItem = service.create(dto.let(mapper::convertDtoToModel)).let(mapper::convertModelToDtoOut)
            ResponseEntity(createdItem, HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    open fun update(id: ID, dto: DTO_IN): ResponseEntity<DTO_OUT> {
        return try {
            check(id != dto.id) {
                throw IllegalArgumentException("Path variable ID and DTO ID do not match.")
            }
            val updatedItem = service.update(dto.let(mapper::convertDtoToModel)).let(mapper::convertModelToDtoOut)
            ResponseEntity(updatedItem, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    open fun delete(id: ID): ResponseEntity<Unit> {
        return try {
            service.deleteById(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    open fun deleteAll(): ResponseEntity<Unit> {
        return try {
            service.deleteAll()
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    open fun addAll(dtos: List<DTO_IN>): ResponseEntity<DTO_OUT> {
        return try {
            service.addAll(dtos.map(mapper::convertDtoToModel))
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    open fun updateAll(dtos: List<DTO_IN>): ResponseEntity<DTO_OUT> {
        return try {
            service.updateAll(dtos.map(mapper::convertDtoToModel))
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}