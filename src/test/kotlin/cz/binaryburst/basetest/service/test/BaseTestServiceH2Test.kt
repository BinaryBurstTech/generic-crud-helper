package cz.binaryburst.basetest.service.test

import cz.binaryburst.generic.exception.EntityIdAlreadyExistException
import cz.binaryburst.generic.exception.EntityNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import cz.binaryburst.basetest.service.dto.BaseTestInsertDto
import cz.binaryburst.basetest.service.mapper.BaseTestMapper
import cz.binaryburst.basetest.service.repository.BaseTestRepository
import cz.binaryburst.basetest.service.service.BaseTestService


@SpringBootTest
@ActiveProfiles("h2mem")
class BaseTestServiceH2Test @Autowired constructor(
    val repository: BaseTestRepository,
    val service: BaseTestService,
    val mapper: BaseTestMapper
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
        val baseTestInsertDto = BaseTestInsertDto(name = "Test")
        val baseTest = service.create(baseTestInsertDto.let(mapper::convertDtoToModel))
        assertNotNull(baseTest)
        assertEquals(1, service.findAll().size)
        assertEquals(baseTestInsertDto.name, baseTest.name)
    }

    @Test
    fun `findById should return existing entity by ID`() {
        val baseTestInsertDto = BaseTestInsertDto(name = "Test")
        val baseTest = service.create(baseTestInsertDto.let(mapper::convertDtoToModel))
        val foundBaseTest = service.findById(baseTest.id)
        assertEquals(baseTest.id, foundBaseTest.id)
        assertEquals(baseTest.name, foundBaseTest.name)
    }

    @Test
    fun `update should modify and persist existing entity`() {
        val baseTestInsertDto = BaseTestInsertDto(name = "Test")
        val baseTest = service.create(baseTestInsertDto.let(mapper::convertDtoToModel))
        val updatedBaseTestInsertDto = baseTestInsertDto.copy(id = baseTest.id, name = "Updated Test")
        service.update(updatedBaseTestInsertDto.let(mapper::convertDtoToModel))
        val updatedBaseTest = service.findById(baseTest.id)
        assertEquals(updatedBaseTestInsertDto.name, updatedBaseTest.name)
    }

    @Test
    fun `deleteById should remove existing entity`() {
        val baseTestInsertDto = BaseTestInsertDto(name = "Test")
        val baseTest = service.create(baseTestInsertDto.let(mapper::convertDtoToModel))
        service.deleteById(baseTest.id)
        assertEquals(0, service.findAll().size)
    }

    @Test
    fun `addAll should persist multiple entities`() {
        val baseTests = listOf(
            BaseTestInsertDto(name = "Test 1"),
            BaseTestInsertDto(name = "Test 2")
        )
        service.addAll(baseTests.map(mapper::convertDtoToModel))
        assertEquals(baseTests.size, service.findAll().size)
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
        val existingEntity = BaseTestInsertDto(name = "Existing Test")
        val createdEntity = service.create(existingEntity.let(mapper::convertDtoToModel))

        val duplicateEntity = BaseTestInsertDto(id = createdEntity.id, name = "Duplicate Test")

        assertThrows(EntityIdAlreadyExistException::class.java) {
            service.create(duplicateEntity.let(mapper::convertDtoToModel))
        }
    }

    @Test
    fun `update should throw EntityNotFoundException for non-existent entity`() {
        val nonExistentDto = BaseTestInsertDto(id = 999, name = "Non-existent Test")

        assertThrows(EntityNotFoundException::class.java) {
            service.update(nonExistentDto.let(mapper::convertDtoToModel))
        }
    }
}
