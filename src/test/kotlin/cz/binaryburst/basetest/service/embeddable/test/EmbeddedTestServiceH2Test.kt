package cz.binaryburst.basetest.service.embeddable.test

import cz.binaryburst.basetest.service.embeddable.dto.EmbeddedTestDtoInput
import cz.binaryburst.basetest.service.embeddable.mapper.EmbeddedTestMapper
import cz.binaryburst.basetest.service.embeddable.repository.EmbeddedTestRepository
import cz.binaryburst.basetest.service.embeddable.service.EmbeddedTestService
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
class EmbeddedTestServiceH2Test @Autowired constructor(
    val repository: EmbeddedTestRepository,
    val service: EmbeddedTestService,
    val mapper: EmbeddedTestMapper
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
        val embeddedTestInsertDto = EmbeddedTestDtoInput(name = "Test")
        val baseTest = service.create(embeddedTestInsertDto.let(mapper::convertDtoToModel))
        assertNotNull(baseTest)
        assertEquals(1, service.findAll().size)
        assertEquals(embeddedTestInsertDto.name, baseTest.name)
    }

    @Test
    fun `findById should return existing entity by ID`() {
        val embeddedTestInsertDto = EmbeddedTestDtoInput(name = "Test")
        val baseTest = service.create(embeddedTestInsertDto.let(mapper::convertDtoToModel))
        val foundBaseTest = service.findById(baseTest.id)
        assertEquals(baseTest.id, foundBaseTest.id)
        assertEquals(baseTest.name, foundBaseTest.name)
    }

    @Test
    fun `update should modify and persist existing entity`() {
        val embeddedTestInsertDto = EmbeddedTestDtoInput(name = "Test")
        val baseTest = service.create(embeddedTestInsertDto.let(mapper::convertDtoToModel))
        val updatedBaseTestInsertDto = embeddedTestInsertDto.copy(id = baseTest.id, name = "Updated Test")
        service.update(updatedBaseTestInsertDto.let(mapper::convertDtoToModel))
        val updatedBaseTest = service.findById(baseTest.id)
        assertEquals(updatedBaseTestInsertDto.name, updatedBaseTest.name)
    }

    @Test
    fun `deleteById should remove existing entity`() {
        val embeddedTestInsertDto = EmbeddedTestDtoInput(name = "Test")
        val baseTest = service.create(embeddedTestInsertDto.let(mapper::convertDtoToModel))
        service.deleteById(baseTest.id)
        assertEquals(0, service.findAll().size)
    }

    @Test
    fun `addAll should persist multiple entities`() {
        val baseTests = listOf(
            EmbeddedTestDtoInput(name = "Test 1"),
            EmbeddedTestDtoInput(name = "Test 2")
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
        val existingEntity = EmbeddedTestDtoInput(name = "Existing Test")
        val createdEntity = service.create(existingEntity.let(mapper::convertDtoToModel))

        val duplicateEntity = EmbeddedTestDtoInput(id = createdEntity.id, name = "Duplicate Test")

        assertThrows(EntityIdAlreadyExistException::class.java) {
            service.create(duplicateEntity.let(mapper::convertDtoToModel))
        }
    }

    @Test
    fun `update should throw EntityNotFoundException for non-existent entity`() {
        val nonExistentDto = EmbeddedTestDtoInput(id = 999, name = "Non-existent Test")

        assertThrows(EntityNotFoundException::class.java) {
            service.update(nonExistentDto.let(mapper::convertDtoToModel))
        }
    }
}
