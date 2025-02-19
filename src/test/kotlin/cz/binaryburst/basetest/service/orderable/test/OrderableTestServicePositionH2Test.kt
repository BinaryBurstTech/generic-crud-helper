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
class OrderableTestServicePositionH2Test @Autowired constructor(
    val repository: OrderableTestRepository,
    val service: OrderableTestService,
    val mapper: OrderableTestMapper
) {

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `create should assign correct position when position is null`() {
        val orderableTestInsertDto1 = OrderableTestDtoInput(name = "Test 1", position = null)
        val orderableTestInsertDto2 = OrderableTestDtoInput(name = "Test 2", position = null)

        val created1 = service.create(orderableTestInsertDto1.let(mapper::convertDtoToModel))
        val created2 = service.create(orderableTestInsertDto2.let(mapper::convertDtoToModel))

        assertEquals(1, created1.position)
        assertEquals(2, created2.position)
    }

    @Test
    fun `create should respect given position`() {
        val orderableTestInsertDto1 = OrderableTestDtoInput(name = "Test 1", position = 5)
        val created = service.create(orderableTestInsertDto1.let(mapper::convertDtoToModel))

        assertEquals(5, created.position)
    }

    @Test
    fun `deleteById should update positions of remaining entities`() {
        val entity1 = service.create(OrderableTestDtoInput(name = "Test 1", position = null).let(mapper::convertDtoToModel))
        val entity2 = service.create(OrderableTestDtoInput(name = "Test 2", position = null).let(mapper::convertDtoToModel))
        val entity3 = service.create(OrderableTestDtoInput(name = "Test 3", position = null).let(mapper::convertDtoToModel))

        assertEquals(1, entity1.position)
        assertEquals(2, entity2.position)
        assertEquals(3, entity3.position)

        service.deleteById(entity2.id)

        val remainingEntities = service.findAll()
        assertEquals(1, remainingEntities[0].position)
        assertEquals(2, remainingEntities[1].position)  // entity3 se posunula nahoru
    }

    @Test
    fun `reorder should correctly change position`() {
        val entity1 = service.create(OrderableTestDtoInput(name = "Test 1", position = null).let(mapper::convertDtoToModel))
        val entity2 = service.create(OrderableTestDtoInput(name = "Test 2", position = null).let(mapper::convertDtoToModel))
        val entity3 = service.create(OrderableTestDtoInput(name = "Test 3", position = null).let(mapper::convertDtoToModel))

        assertEquals(1, entity1.position)
        assertEquals(2, entity2.position)
        assertEquals(3, entity3.position)

        val updatedEntity2 = entity2.copy(position = 1)
        service.reorder(updatedEntity2)

        val reorderedEntities = service.findAll()
        assertEquals(1, reorderedEntities[0].position)
        assertEquals(2, reorderedEntities[1].position)
        assertEquals(3, reorderedEntities[2].position)

        assertEquals(entity2.id, reorderedEntities[0].id)  // entity2 se přesunula na první místo
        assertEquals(entity1.id, reorderedEntities[1].id)  // entity1 se posunula na druhé
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
