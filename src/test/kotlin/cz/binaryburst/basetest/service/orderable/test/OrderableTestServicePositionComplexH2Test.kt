package cz.binaryburst.basetest.service.orderable.test

import cz.binaryburst.basetest.service.orderable.dto.OrderableTestDtoInput
import cz.binaryburst.basetest.service.orderable.mapper.OrderableTestMapper
import cz.binaryburst.basetest.service.orderable.repository.OrderableTestRepository
import cz.binaryburst.basetest.service.orderable.service.OrderableTestService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("h2mem")
class OrderableTestServicePositionComplexH2Test @Autowired constructor(
    val repository: OrderableTestRepository,
    val service: OrderableTestService,
    val mapper: OrderableTestMapper
) {

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `reorder should correctly move entity to last position`() {
        val entities = (1..5).map {
            service.createOrderable(OrderableTestDtoInput(name = "Test $it", position = null).let(mapper::convertDtoToModel), null)
        }

        val entityToMove = entities[1].copy(position = 5) // Přesun na konec
        service.reorder(entityToMove, null)

        val orderedEntities = service.findAllOrderable(null)

        assertEquals(1, orderedEntities[0].position)
        assertEquals(5, orderedEntities.last().position)
        assertEquals(entityToMove.id, orderedEntities.last().id)
    }

    @Test
    fun `reorder should correctly move entity to first position`() {
        val entities = (1..5).map {
            service.createOrderable(OrderableTestDtoInput(name = "Test $it", position = null).let(mapper::convertDtoToModel), null)
        }

        val entityToMove = entities[3].copy(position = 1) // Přesun na začátek
        service.reorder(entityToMove, null)

        val orderedEntities = service.findAllOrderable(null)

        assertEquals(1, orderedEntities[0].position)
        assertEquals(entityToMove.id, orderedEntities[0].id)
    }

    @Test
    fun `reorder should correctly move entity to middle`() {
        val entities = (1..5).map {
            service.createOrderable(OrderableTestDtoInput(name = "Test $it", position = null).let(mapper::convertDtoToModel), null)
        }

        val entityToMove = entities[4].copy(position = 3) // Přesun do středu
        service.reorder(entityToMove, null)

        val orderedEntities = service.findAllOrderable(null)

        assertEquals(3, orderedEntities[2].position)
        assertEquals(entityToMove.id, orderedEntities[2].id)
    }

    @Test
    fun `adding entity between two should shift positions`() {
        val entity1 =
            service.createOrderable(OrderableTestDtoInput(name = "Test 1", position = 1).let(mapper::convertDtoToModel), null)
        val entity2 =
            service.createOrderable(OrderableTestDtoInput(name = "Test 2", position = 2).let(mapper::convertDtoToModel), null)

        val newEntity =
            service.createOrderable(OrderableTestDtoInput(name = "New Test", position = 2).let(mapper::convertDtoToModel), null)

        val orderedEntities = service.findAllOrderable(null)

        assertEquals(1, orderedEntities[0].position)
        assertEquals(newEntity.id, orderedEntities[1].id)
        assertEquals(3, orderedEntities[2].position) // entity2 se posunula nahoru
    }

    @Test
    fun `batch creation and reorder should keep positions consistent`() {
        val createdEntities = service.addAllOrderable((1..10).map {
            OrderableTestDtoInput(name = "Batch $it", position = null).let(mapper::convertDtoToModel)
        }, null)

        assertEquals(10, createdEntities.size)

        val entityToMove = createdEntities[5].copy(position = 2)
        service.reorder(entityToMove, null)

        val orderedEntities = service.findAllOrderable(null)

        assertEquals(entityToMove.id, orderedEntities[1].id)
        assertEquals(10, orderedEntities.size)
    }

    @Test
    fun `batch addition and deletion should keep positions valid`() {
        val createdEntities = service.addAllOrderable((1..5).map {
            OrderableTestDtoInput(name = "Batch $it", position = null).let(mapper::convertDtoToModel)
        }, null)

        service.deleteOrderableById(createdEntities[2].id, null) // Smazání entity na pozici 3

        val remainingEntities = service.findAllOrderable(null)

        assertEquals(4, remainingEntities.size)
        assertEquals(1, remainingEntities[0].position)
        assertEquals(4, remainingEntities.last().position)
    }

    @Test
    fun `deleteAll should reset positions correctly`() {
        service.addAllOrderable((1..5).map {
            OrderableTestDtoInput(name = "Batch $it", position = null).let(mapper::convertDtoToModel)
        }, null)

        service.deleteOrderableAll(null)

        val remainingEntities = service.findAllOrderable(null)
        assertEquals(0, remainingEntities.size)
    }

    @Test
    fun `mass delete should not leave gaps in position sequence`() {
        val createdEntities = service.addAllOrderable((1..5).map {
            OrderableTestDtoInput(name = "Batch $it", position = null).let(mapper::convertDtoToModel)
        }, null)

        service.deleteOrderableById(createdEntities[1].id, null)
        service.deleteOrderableById(createdEntities[3].id, null)

        val remainingEntities = service.findAllOrderable(null)

        assertEquals(3, remainingEntities.size)
        assertEquals(1, remainingEntities[0].position)
        assertEquals(2, remainingEntities[1].position)
        assertEquals(3, remainingEntities[2].position) // Gaps se vyplní
    }
}
