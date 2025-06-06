package cz.binaryburst.basetest.service.treestructure

import cz.binaryburst.treestructure.group.dto.GroupDtoInput
import cz.binaryburst.treestructure.group.dto.GroupParams
import cz.binaryburst.treestructure.group.mapper.GroupMapper
import cz.binaryburst.treestructure.group.repository.GroupRepository
import cz.binaryburst.treestructure.group.service.GroupService
import cz.binaryburst.treestructure.root.dto.RootDtoInput
import cz.binaryburst.treestructure.root.service.RootService
import cz.binaryburst.treestructure.zone.dto.ZoneDtoInput
import cz.binaryburst.treestructure.zone.service.ZoneService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("h2mem")
class GroupServiceH2Test @Autowired constructor(
    val groupRepository: GroupRepository,
    val groupService: GroupService,
    val groupMapper: GroupMapper,
    val rootService: RootService,
    val zoneService: ZoneService
) {

    private var zoneId: Long = 0

    @BeforeEach
    fun setup() {
        groupRepository.deleteAll()
        
        // Create test root and zone
        val root = rootService.create(RootDtoInput(name = "Test Root").let { 
            rootService.mapper.convertDtoToModel(it) 
        })
        val zone = zoneService.create(ZoneDtoInput(name = "Test Zone", rootId = root.id).let { 
            zoneService.mapper.convertDtoToModel(it) 
        })
        zoneId = zone.id
    }

    @Test
    fun `create should assign correct position when position is null`() {
        val groupDto1 = GroupDtoInput(name = "Group 1", position = null, zoneId = zoneId)
        val groupDto2 = GroupDtoInput(name = "Group 2", position = null, zoneId = zoneId)

        val created1 = groupService.createOrderable(groupDto1.let(groupMapper::convertDtoToModel), GroupParams(zoneId))
        val created2 = groupService.createOrderable(groupDto2.let(groupMapper::convertDtoToModel), GroupParams(zoneId))

        assertEquals(1, created1.position)
        assertEquals(2, created2.position)
    }

    @Test
    fun `deleteById should update positions of remaining entities`() {
        val group1 = groupService.createOrderable(
            GroupDtoInput(name = "Group 1", position = null, zoneId = zoneId).let(groupMapper::convertDtoToModel),
            GroupParams(zoneId)
        )
        val group2 = groupService.createOrderable(
            GroupDtoInput(name = "Group 2", position = null, zoneId = zoneId).let(groupMapper::convertDtoToModel),
            GroupParams(zoneId)
        )
        val group3 = groupService.createOrderable(
            GroupDtoInput(name = "Group 3", position = null, zoneId = zoneId).let(groupMapper::convertDtoToModel),
            GroupParams(zoneId)
        )

        assertEquals(1, group1.position)
        assertEquals(2, group2.position)
        assertEquals(3, group3.position)

        groupService.deleteOrderableById(group2.id, GroupParams(zoneId))

        val remainingGroups = groupService.findAllOrderable(Pageable.unpaged(), GroupParams(zoneId))
        assertEquals(2, remainingGroups.content.size)
        assertEquals(1, remainingGroups.content[0].position)
        assertEquals(2, remainingGroups.content[1].position) // group3 moved down
    }

    @Test
    fun `reorder should correctly change position`() {
        val group1 = groupService.createOrderable(
            GroupDtoInput(name = "Group 1", position = null, zoneId = zoneId).let(groupMapper::convertDtoToModel),
            GroupParams(zoneId)
        )
        val group2 = groupService.createOrderable(
            GroupDtoInput(name = "Group 2", position = null, zoneId = zoneId).let(groupMapper::convertDtoToModel),
            GroupParams(zoneId)
        )
        val group3 = groupService.createOrderable(
            GroupDtoInput(name = "Group 3", position = null, zoneId = zoneId).let(groupMapper::convertDtoToModel),
            GroupParams(zoneId)
        )

        assertEquals(1, group1.position)
        assertEquals(2, group2.position)
        assertEquals(3, group3.position)

        // Move group3 to position 1
        val updatedGroup3 = group3.copy(position = 1)
        groupService.reorder(updatedGroup3, GroupParams(zoneId))

        val reorderedGroups = groupService.findAllOrderable(Pageable.unpaged(), GroupParams(zoneId))
        assertEquals(1, reorderedGroups.content[0].position)
        assertEquals(group3.id, reorderedGroups.content[0].id) // group3 is now first
        assertEquals(2, reorderedGroups.content[1].position)
        assertEquals(group1.id, reorderedGroups.content[1].id) // group1 moved to second
        assertEquals(3, reorderedGroups.content[2].position)
        assertEquals(group2.id, reorderedGroups.content[2].id) // group2 moved to third
    }

    @Test
    fun `batch creation should assign sequential positions`() {
        val groups = (1..5).map {
            GroupDtoInput(name = "Batch Group $it", position = null, zoneId = zoneId).let(groupMapper::convertDtoToModel)
        }

        val createdGroups = groupService.addAllOrderable(groups, GroupParams(zoneId))

        assertEquals(5, createdGroups.size)
        createdGroups.forEachIndexed { index, group ->
            assertEquals(index + 1, group.position)
        }
    }

    @Test
    fun `findByZoneIdOrderByPosition should return groups in correct order`() {
        val group3 = groupService.createOrderable(
            GroupDtoInput(name = "Group 3", position = 3, zoneId = zoneId).let(groupMapper::convertDtoToModel),
            null
        )
        val group1 = groupService.createOrderable(
            GroupDtoInput(name = "Group 1", position = 1, zoneId = zoneId).let(groupMapper::convertDtoToModel),
            null
        )
        val group2 = groupService.createOrderable(
            GroupDtoInput(name = "Group 2", position = 2, zoneId = zoneId).let(groupMapper::convertDtoToModel),
            null
        )

        val orderedGroups = groupService.findByZoneIdOrderByPosition(zoneId)

        assertEquals(3, orderedGroups.size)
        assertEquals("Group 1", orderedGroups[0].name)
        assertEquals("Group 2", orderedGroups[1].name)
        assertEquals("Group 3", orderedGroups[2].name)
    }

    @Test
    fun `groups in different zones should have independent positions`() {
        // Create second zone
        val zone2 = zoneService.create(ZoneDtoInput(name = "Test Zone 2", rootId = 1).let { 
            zoneService.mapper.convertDtoToModel(it) 
        })

        // Create groups in zone 1
        val group1Zone1 = groupService.createOrderable(
            GroupDtoInput(name = "Zone1 Group1", position = null, zoneId = zoneId).let(groupMapper::convertDtoToModel),
            GroupParams(zoneId)
        )
        val group2Zone1 = groupService.createOrderable(
            GroupDtoInput(name = "Zone1 Group2", position = null, zoneId = zoneId).let(groupMapper::convertDtoToModel),
            GroupParams(zoneId)
        )

        // Create groups in zone 2
        val group1Zone2 = groupService.createOrderable(
            GroupDtoInput(name = "Zone2 Group1", position = null, zoneId = zone2.id).let(groupMapper::convertDtoToModel),
            GroupParams(zone2.id)
        )
        val group2Zone2 = groupService.createOrderable(
            GroupDtoInput(name = "Zone2 Group2", position = null, zoneId = zone2.id).let(groupMapper::convertDtoToModel),
            GroupParams(zone2.id)
        )

        // Both zones should have groups with positions 1 and 2
        assertEquals(1, group1Zone1.position)
        assertEquals(2, group2Zone1.position)
        assertEquals(1, group1Zone2.position)
        assertEquals(2, group2Zone2.position)

        // Verify each zone has correct number of groups
        val zone1Groups = groupService.findAllOrderable(Pageable.unpaged(), GroupParams(zoneId))
        val zone2Groups = groupService.findAllOrderable(Pageable.unpaged(), GroupParams(zone2.id))
        
        assertEquals(2, zone1Groups.content.size)
        assertEquals(2, zone2Groups.content.size)
    }
}