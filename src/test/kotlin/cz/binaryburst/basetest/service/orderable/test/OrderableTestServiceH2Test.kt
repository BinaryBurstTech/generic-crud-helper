package cz.binaryburst.basetest.service.orderable.test

import cz.binaryburst.basetest.service.orderable.dto.OrderableTestDtoInput
import cz.binaryburst.basetest.service.orderable.mapper.OrderableTestMapper
import cz.binaryburst.basetest.service.orderable.repository.OrderableTestRepository
import cz.binaryburst.basetest.service.orderable.service.OrderableTestService
import cz.binaryburst.generic.exception.EntityIdAlreadyExistException
import cz.binaryburst.generic.exception.EntityNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("h2mem")
class OrderableTestServiceH2Test @Autowired constructor(
    val repository: OrderableTestRepository,
    val service: OrderableTestService,
    val mapper: OrderableTestMapper
) {

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `findAll should return empty list when no entities exist`() {
        val result = service.findAll()
        assertEquals(0, result.size)
    }

    @Test
    fun `create should persist and return new entity`() {
        val orderableTestInsertDto = OrderableTestDtoInput(name = "Test", position = null)
        val orderableTest = service.create(orderableTestInsertDto.let(mapper::convertDtoToModel))
        assertNotNull(orderableTest)
        assertEquals(1, service.findAll().size)
        assertEquals(orderableTestInsertDto.name, orderableTest.name)
    }

    @Test
    fun `findById should return existing entity by ID`() {
        val orderableTestInsertDto = OrderableTestDtoInput(name = "Test", position = null)
        val orderableTest = service.create(orderableTestInsertDto.let(mapper::convertDtoToModel))
        val foundOrderableTest = service.findById(orderableTest.id)
        assertEquals(orderableTest.id, foundOrderableTest.id)
        assertEquals(orderableTest.name, foundOrderableTest.name)
    }

    @Test
    fun `update should modify and persist existing entity`() {
        val orderableTestInsertDto = OrderableTestDtoInput(name = "Test", position = null)
        val orderableTest = service.create(orderableTestInsertDto.let(mapper::convertDtoToModel))
        val updatedOrderableTestInsertDto = orderableTestInsertDto.copy(id = orderableTest.id, name = "Updated Test")
        service.update(updatedOrderableTestInsertDto.let(mapper::convertDtoToModel))
        val updatedOrderableTest = service.findById(orderableTest.id)
        assertEquals(updatedOrderableTestInsertDto.name, updatedOrderableTest.name)
    }

    @Test
    fun `deleteById should remove existing entity`() {
        val orderableTestInsertDto = OrderableTestDtoInput(name = "Test", position = null)
        val orderableTest = service.create(orderableTestInsertDto.let(mapper::convertDtoToModel))
        service.deleteById(orderableTest.id)
        assertEquals(0, service.findAll().size)
    }

    @Test
    fun `addAll should persist multiple entities`() {
        val orderableTest = listOf(
            OrderableTestDtoInput(name = "Test 1", position = null),
            OrderableTestDtoInput(name = "Test 2", position = null),
        )
        service.addAll(orderableTest.map(mapper::convertDtoToModel))
        assertEquals(orderableTest.size, service.findAll().size)
    }

    @Test
    fun `deleteAll should remove all entities`() {
        service.deleteAll()
        assertEquals(0, service.findAll().size)
    }

    @Test
    fun `findById should throw EntityNotFoundException for non-existent entity`() {
        val nonExistentId = 999L
        assertThrows(EntityNotFoundException::class.java) {
            service.findById(nonExistentId)
        }
    }

    @Test
    fun `create should throw EntityIdAlreadyExistException for existing entity ID`() {
        val existingEntity = OrderableTestDtoInput(name = "Existing Test", position = null)
        val createdEntity = service.create(existingEntity.let(mapper::convertDtoToModel))

        val duplicateEntity = OrderableTestDtoInput(id = createdEntity.id, name = "Duplicate Test", position = null)

        assertThrows(EntityIdAlreadyExistException::class.java) {
            service.create(duplicateEntity.let(mapper::convertDtoToModel))
        }
    }

    @Test
    fun `update should throw EntityNotFoundException for non-existent entity`() {
        val nonExistentDto = OrderableTestDtoInput(id = 999, name = "Non-existent Test", position = null)

        assertThrows(EntityNotFoundException::class.java) {
            service.update(nonExistentDto.let(mapper::convertDtoToModel))
        }
    }
}
