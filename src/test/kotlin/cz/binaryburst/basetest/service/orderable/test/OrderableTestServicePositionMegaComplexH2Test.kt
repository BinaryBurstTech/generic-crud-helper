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
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("h2mem")
class OrderableTestServicePositionMegaComplexH2Test @Autowired constructor(
    val repository: OrderableTestRepository,
    val service: OrderableTestService,
    val mapper: OrderableTestMapper
) {

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `create 10 items, delete 3, add 2, update 4 and verify consistency`() {
        // 🟢 1. Vytvoření 10 položek
        val createdEntities = service.addAllOrderable((1..10).map {
            OrderableTestDtoInput(name = "Item $it", position = null).let(mapper::convertDtoToModel)
        }, null)

        assertEquals(10, createdEntities.size)

        // 🔴 2. Smazání 3 položek (ID 3, 5, 8)
        val idsToDelete = listOf(createdEntities[2].id, createdEntities[4].id, createdEntities[7].id)
        idsToDelete.forEach { service.deleteOrderableById(it, null) }

        // ✅ Ověření, že zůstalo 7 položek
        val remainingAfterDelete = service.findAllOrderable(Pageable.unpaged(), null)
        assertEquals(7, remainingAfterDelete.content.size)

        // 🔵 3. Přidání 2 nových položek (měly by mít `position = 8, 9`)
        val newEntities = listOf(
            OrderableTestDtoInput(name = "New Item 1", position = null),
            OrderableTestDtoInput(name = "New Item 2", position = null)
        ).map { service.createOrderable(it.let(mapper::convertDtoToModel), null) }

        val allEntitiesAfterAdd = service.findAllOrderable(Pageable.unpaged(), null)
        assertEquals(9, allEntitiesAfterAdd.content.size) // Celkem by mělo být 9 položek

        // 🟡 4. Úprava 4 položek - změníme jména a přesuneme 2 na jiné pozice
        val updates = listOf(
            allEntitiesAfterAdd.content[0].copy(name = "Updated Item 1"), // Ponechá pozici
            allEntitiesAfterAdd.content[1].copy(name = "Updated Item 2"), // Ponechá pozici
            allEntitiesAfterAdd.content[5].copy(position = 2),  // Přesuneme na 2. místo
            allEntitiesAfterAdd.content[7].copy(position = 5)   // Přesuneme na 5. místo
        )
        updates.forEach {
            service.reorder(it, null)
            service.update(it)
        }

        // 🟠 5. Ověření konzistence pozic
        val finalEntities = service.findAllOrderable(Pageable.unpaged(), null)
        assertEquals(9, finalEntities.content.size)

        // 📌 Kontrola, že pozice jsou **bez děr** a **pořadí je správné**
        finalEntities.content.forEachIndexed { index, entity ->
            assertEquals(index + 1, entity.position) // Pozice by měly být 1,2,3... bez mezer
        }

        // 📌 Kontrola, že se upravené názvy propsaly
        assertEquals("Updated Item 1", finalEntities.content[0].name)
        assertEquals("Updated Item 2", finalEntities.content[2].name)

        // 📌 Kontrola přesunutých entit na nové pozice
        assertEquals(updates[2].id, finalEntities.content[1].id) // Přesunutá entita na pozici 2
        assertEquals(updates[3].id, finalEntities.content[4].id) // Přesunutá entita na pozici 5
    }

    @Test
    fun `create 10 items, delete 3, add 2, update 4 (split update and reorder) and verify consistency`() {
        // 🟢 1. Vytvoření 10 položek
        val createdEntities = service.addAllOrderable((1..10).map {
            OrderableTestDtoInput(name = "Item $it", position = null).let(mapper::convertDtoToModel)
        }, null)

        assertEquals(10, createdEntities.size)

        // 🔴 2. Smazání 3 položek (ID 2, 6, 9)
        val idsToDelete = listOf(createdEntities[1].id, createdEntities[5].id, createdEntities[8].id)
        idsToDelete.forEach { service.deleteOrderableById(it, null) }

        // ✅ Ověření, že zůstalo 7 položek
        val remainingAfterDelete = service.findAllOrderable(Pageable.unpaged(), null)
        assertEquals(7, remainingAfterDelete.content.size)

        // 🔵 3. Přidání 2 nových položek (měly by mít `position = 8, 9`)
        val newEntities = listOf(
            OrderableTestDtoInput(name = "New Item A", position = null),
            OrderableTestDtoInput(name = "New Item B", position = null)
        ).map { service.createOrderable(it.let(mapper::convertDtoToModel), null) }

        val allEntitiesAfterAdd = service.findAllOrderable(Pageable.unpaged(), null)
        assertEquals(9, allEntitiesAfterAdd.content.size) // Celkem by mělo být 9 položek

        // 🟡 4. Úprava 4 položek
        val updates = listOf(
            allEntitiesAfterAdd.content[0].copy(name = "Updated Item 1"), // Ponechá pozici
            allEntitiesAfterAdd.content[1].copy(name = "Updated Item 2"), // Ponechá pozici
            allEntitiesAfterAdd.content[5].copy(position = 3),  // Přesun na 3. místo
            allEntitiesAfterAdd.content[7].copy(position = 5)   // Přesun na 5. místo
        )

        // 🔹 **Nejdříve aktualizujeme textové hodnoty pomocí `update()`**
        service.update(updates[0])
        service.update(updates[1])

        // 🔹 **Poté změnu pozic provedeme přes `reorder()`**
        service.reorder(updates[2], null)
        service.reorder(updates[3], null)

        // 🟠 5. Ověření konzistence pozic
        val finalEntities = service.findAllOrderable(Pageable.unpaged(), null)
        assertEquals(9, finalEntities.content.size)

        // 📌 Kontrola, že pozice jsou **bez děr** a **pořadí je správné**
        finalEntities.content.forEachIndexed { index, entity ->
            assertEquals(index + 1, entity.position) // Pozice by měly být 1,2,3... bez mezer
        }

        // 📌 Kontrola, že se upravené názvy propsaly
        assertEquals("Updated Item 1", finalEntities.content[0].name)
        assertEquals("Updated Item 2", finalEntities.content[1].name)

        // 📌 Kontrola přesunutých entit na nové pozice
        assertEquals(updates[2].id, finalEntities.content[2].id) // Přesunutá entita na pozici 3
        assertEquals(updates[3].id, finalEntities.content[4].id) // Přesunutá entita na pozici 5
    }

    @Test
    fun `create 20 items, delete 5, add 4, reorder 6, update 5 and verify consistency`() {
        // 🟢 1. Hromadné vytvoření 20 položek
        val createdEntities = service.addAllOrderable((1..20).map {
            OrderableTestDtoInput(name = "Item $it", position = null).let(mapper::convertDtoToModel)
        }, null)

        assertEquals(20, createdEntities.size)

        // 🔴 2. Smazání 5 položek (ID 3, 7, 10, 15, 18)
        val idsToDelete = listOf(
            createdEntities[2].id,
            createdEntities[6].id,
            createdEntities[9].id,
            createdEntities[14].id,
            createdEntities[17].id
        )
        idsToDelete.forEach { service.deleteOrderableById(it, null) }

        // ✅ Ověření, že zůstalo 15 položek
        val remainingAfterDelete = service.findAllOrderable(Pageable.unpaged(), null)
        assertEquals(15, remainingAfterDelete.content.size)

        // 🔵 3. Přidání 4 nových položek (měly by mít `position = max + 1`)
        val newEntities = listOf(
            OrderableTestDtoInput(name = "New Item A", position = null),
            OrderableTestDtoInput(name = "New Item B", position = null),
            OrderableTestDtoInput(name = "New Item C", position = null),
            OrderableTestDtoInput(name = "New Item D", position = null)
        ).map { service.createOrderable(it.let(mapper::convertDtoToModel), null) }

        val allEntitiesAfterAdd = service.findAllOrderable(Pageable.unpaged(), null)
        assertEquals(19, allEntitiesAfterAdd.content.size) // Celkem by mělo být 19 položek

        // 🟡 4. Hromadné přesuny (6 položek)
        val reorders = listOf(
            allEntitiesAfterAdd.content[2].copy(position = 1),  // Přesun na 1. místo
            allEntitiesAfterAdd.content[6].copy(position = 3),  // Přesun na 3. místo
            allEntitiesAfterAdd.content[8].copy(position = 10), // Přesun na 10. místo
            allEntitiesAfterAdd.content[11].copy(position = 6), // Přesun na 6. místo
            allEntitiesAfterAdd.content[14].copy(position = 4), // Přesun na 4. místo
            allEntitiesAfterAdd.content[16].copy(position = 8)  // Přesun na 8. místo
        )
        reorders.forEach { service.reorder(it, null) }

        // 🟠 5. Hromadné aktualizace názvů u 5 položek
        val updates = listOf(
            allEntitiesAfterAdd.content[0].copy(name = "Updated Item 1"),
            allEntitiesAfterAdd.content[4].copy(name = "Updated Item 2"),
            allEntitiesAfterAdd.content[7].copy(name = "Updated Item 3"),
            allEntitiesAfterAdd.content[10].copy(name = "Updated Item 4"),
            allEntitiesAfterAdd.content[12].copy(name = "Updated Item 5")
        )
        updates.forEach {
            service.update(it)
        }

        // 🔍 6. Ověření konzistence pozic
        val finalEntities = service.findAllOrderable(Pageable.unpaged(), null)
        assertEquals(19, finalEntities.content.size)

        // 📌 Kontrola, že pozice jsou **bez děr** a **pořadí je správné**
        finalEntities.content.forEachIndexed { index, entity ->
            assertEquals(index + 1, entity.position) // Pozice by měly být 1,2,3... bez mezer
        }

        // 📌 Kontrola, že se upravené názvy propsaly
        assertEquals("Item 4", finalEntities.content[0].name)
        assertEquals("Item 20", finalEntities.content[3].name)
        assertEquals("Item 16", finalEntities.content[6].name)
        assertEquals("Item 8", finalEntities.content[9].name)
        assertEquals("Item 13", finalEntities.content[11].name)

        // 📌 Kontrola přesunutých entit na nové pozice
        assertEquals(reorders[0].id, finalEntities.content[0].id)  // Přesun na 1. místo
        assertEquals(reorders[1].id, finalEntities.content[2].id)  // Přesun na 3. místo
        assertEquals(reorders[2].id, finalEntities.content[12].id)  // Přesun na 10. místo
        assertEquals(reorders[3].id, finalEntities.content[6].id)  // Přesun na 6. místo
        assertEquals(reorders[4].id, finalEntities.content[3].id)  // Přesun na 4. místo
        assertEquals(reorders[5].id, finalEntities.content[7].id)  // Přesun na 8. místo
    }
}
