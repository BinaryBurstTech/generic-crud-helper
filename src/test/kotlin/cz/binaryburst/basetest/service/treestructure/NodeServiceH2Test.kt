package cz.binaryburst.basetest.service.treestructure


import cz.binaryburst.treestructure.group.dto.GroupDtoInput
import cz.binaryburst.treestructure.group.dto.GroupParams
import cz.binaryburst.treestructure.group.service.GroupService
import cz.binaryburst.treestructure.node.dto.FileDtoInput
import cz.binaryburst.treestructure.node.dto.FolderDtoInput
import cz.binaryburst.treestructure.node.dto.NodeParams
import cz.binaryburst.treestructure.node.mapper.NodeMapper
import cz.binaryburst.treestructure.node.repository.NodeRepository
import cz.binaryburst.treestructure.node.service.NodeService
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
class NodeServiceH2Test @Autowired constructor(
    val nodeRepository: NodeRepository,
    val nodeService: NodeService,
    val nodeMapper: NodeMapper,
    val rootService: RootService,
    val zoneService: ZoneService,
    val groupService: GroupService
) {

    private var groupId: Long = 0
    private var folderId: Long = 0

    @BeforeEach
    fun setup() {
        nodeRepository.deleteAll()
        
        // Create test hierarchy
        val root = rootService.create(RootDtoInput(name = "Test Root").let {
            rootService.mapper.convertDtoToModel(it) 
        })
        val zone = zoneService.create(ZoneDtoInput(name = "Test Zone", rootId = root.id).let {
            zoneService.mapper.convertDtoToModel(it) 
        })
        val group = groupService.createOrderable(
            GroupDtoInput(name = "Test Group", position = null, zoneId = zone.id).let(groupService.mapper::convertDtoToModel),
            GroupParams(zone.id)
        )
        groupId = group.id

        // Create a test folder for nested tests
        val folder = nodeService.createOrderable(
            FolderDtoInput(name = "Test Folder", position = null, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, null)
        )
        folderId = folder.id
    }

    @Test
    fun `create root level nodes should assign correct positions`() {
        val file1 = FolderDtoInput(name = "Root File 1", position = null, groupId = groupId)
        val file2 = FileDtoInput(name = "Root File 2", position = null, groupId = groupId)

        val created1 = nodeService.createOrderable(file1.let(nodeMapper::convertDtoToModel), NodeParams(groupId, null))
        val created2 = nodeService.createOrderable(file2.let(nodeMapper::convertDtoToModel), NodeParams(groupId, null))

        // Should be positions 2 and 3 since folder already exists at position 1
        assertEquals(2, created1.position)
        assertEquals(3, created2.position)
    }

    @Test
    fun `create nested nodes should assign correct positions`() {
        val nestedFile1 = FileDtoInput(name = "Nested File 1", position = null, parentId = folderId, groupId = groupId)
        val nestedFile2 = FileDtoInput(name = "Nested File 2", position = null, parentId = folderId, groupId = groupId)

        val created1 = nodeService.createOrderable(nestedFile1.let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, folderId)
        )
        val created2 = nodeService.createOrderable(nestedFile2.let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, folderId)
        )

        assertEquals(1, created1.position)
        assertEquals(2, created2.position)
    }

    @Test
    fun `delete node should update positions of remaining nodes`() {
        // Create 3 nested nodes
        val node1 = nodeService.createOrderable(
            FileDtoInput(name = "Node 1", position = null, parentId = folderId, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, folderId)
        )
        val node2 = nodeService.createOrderable(
            FileDtoInput(name = "Node 2", position = null, parentId = folderId, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, folderId)
        )
        val node3 = nodeService.createOrderable(
            FileDtoInput(name = "Node 3", position = null, parentId = folderId, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, folderId)
        )

        assertEquals(1, node1.position)
        assertEquals(2, node2.position)
        assertEquals(3, node3.position)

        // Delete middle node
        nodeService.deleteOrderableById(node2.id, NodeParams(groupId, folderId))

        val remainingNodes = nodeService.findAllOrderable(Pageable.unpaged(), NodeParams(groupId, folderId))
        assertEquals(2, remainingNodes.content.size)
        assertEquals(1, remainingNodes.content[0].position)
        assertEquals(2, remainingNodes.content[1].position) // node3 moved down
        assertEquals(node3.id, remainingNodes.content[1].id)
    }

    @Test
    fun `reorder should correctly change position within same parent`() {
        // Create 3 nested nodes
        val node1 = nodeService.createOrderable(
            FileDtoInput(name = "Node 1", position = null, parentId = folderId, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, folderId)
        )
        val node2 = nodeService.createOrderable(
            FileDtoInput(name = "Node 2", position = null, parentId = folderId, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, folderId)
        )
        val node3 = nodeService.createOrderable(
            FileDtoInput(name = "Node 3", position = null, parentId = folderId, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, folderId)
        )

        // Move node3 to position 1
        val updatedNode3 = node3.apply { position = 1}
        nodeService.reorder(updatedNode3, NodeParams(groupId, folderId))

        val reorderedNodes = nodeService.findAllOrderable(Pageable.unpaged(), NodeParams(groupId, folderId))
        assertEquals(1, reorderedNodes.content[0].position)
        assertEquals(node3.id, reorderedNodes.content[0].id) // node3 is now first
        assertEquals(2, reorderedNodes.content[1].position)
        assertEquals(node1.id, reorderedNodes.content[1].id) // node1 moved to second
        assertEquals(3, reorderedNodes.content[2].position)
        assertEquals(node2.id, reorderedNodes.content[2].id) // node2 moved to third
    }

    @Test
    fun `positions should be independent between different parents`() {
        // Create second folder
        val folder2 = nodeService.createOrderable(
            FolderDtoInput(name = "Folder 2", position = null, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, null)
        )

        // Create nodes in folder1
        val node1Folder1 = nodeService.createOrderable(
            FileDtoInput(name = "F1 Node1", position = null, parentId = folderId, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, folderId)
        )
        val node2Folder1 = nodeService.createOrderable(
            FileDtoInput(name = "F1 Node2", position = null, parentId = folderId, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, folderId)
        )

        // Create nodes in folder2
        val node1Folder2 = nodeService.createOrderable(
            FileDtoInput(name = "F2 Node1", position = null, parentId = folder2.id, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, folder2.id)
        )
        val node2Folder2 = nodeService.createOrderable(
            FileDtoInput(name = "F2 Node2", position = null, parentId = folder2.id, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, folder2.id)
        )

        // Both folders should have nodes with positions 1 and 2
        assertEquals(1, node1Folder1.position)
        assertEquals(2, node2Folder1.position)
        assertEquals(1, node1Folder2.position)
        assertEquals(2, node2Folder2.position)

        // Verify each folder has correct number of nodes
        val folder1Nodes = nodeService.findAllOrderable(Pageable.unpaged(), NodeParams(groupId, folderId))
        val folder2Nodes = nodeService.findAllOrderable(Pageable.unpaged(), NodeParams(groupId, folder2.id))
        
        assertEquals(2, folder1Nodes.content.size)
        assertEquals(2, folder2Nodes.content.size)
    }

    @Test
    fun `positions should be independent between different groups`() {
        // Create second group
        val group2 = groupService.createOrderable(
            GroupDtoInput(name = "Group 2", position = null, zoneId = 1).let(groupService.mapper::convertDtoToModel),
            GroupParams(1)
        )

        // Create nodes in group1
        val node1Group1 = nodeService.createOrderable(
            FileDtoInput(name = "G1 Node1", position = null, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, null)
        )
        val node2Group1 = nodeService.createOrderable(
            FileDtoInput(name = "G1 Node2", position = null, groupId = groupId).let(nodeMapper::convertDtoToModel),
            NodeParams(groupId, null)
        )

        // Create nodes in group2
        val node1Group2 = nodeService.createOrderable(
            FileDtoInput(name = "G2 Node1", position = null, groupId = group2.id).let(nodeMapper::convertDtoToModel),
            NodeParams(group2.id, null)
        )
        val node2Group2 = nodeService.createOrderable(
            FileDtoInput(name = "G2 Node2", position = null, groupId = group2.id).let(nodeMapper::convertDtoToModel),
            NodeParams(group2.id, null)
        )

        // Both groups should have nodes starting from position 2 (since each has a folder at position 1)
        assertEquals(2, node1Group1.position)
        assertEquals(3, node2Group1.position)
        assertEquals(1, node1Group2.position)
        assertEquals(2, node2Group2.position)

        // Verify each group has correct number of nodes
        val group1Nodes = nodeService.findAllOrderable(Pageable.unpaged(), NodeParams(groupId, null))
        val group2Nodes = nodeService.findAllOrderable(Pageable.unpaged(), NodeParams(group2.id, null))
        
        assertEquals(3, group1Nodes.content.size) // folder + 2 nodes
        assertEquals(2, group2Nodes.content.size) // 2 nodes
    }

    @Test
    fun `batch creation should assign sequential positions`() {
        val nodes = (1..5).map {
            FileDtoInput(name = "Batch Node $it", position = null, parentId = folderId, groupId = groupId).let(nodeMapper::convertDtoToModel)
        }

        val createdNodes = nodeService.addAllOrderable(nodes, NodeParams(groupId, folderId))

        assertEquals(5, createdNodes.size)
        createdNodes.forEachIndexed { index, node ->
            assertEquals(index + 1, node.position)
        }
    }

    @Test
    fun `findByParentIdOrderByPosition should return nodes in correct order`() {
        val node3 = nodeService.createOrderable(
            FileDtoInput(name = "Node 3", position = 3, parentId = folderId, groupId = groupId).let(nodeMapper::convertDtoToModel),
            null
        )
        val node1 = nodeService.createOrderable(
            FileDtoInput(name = "Node 1", position = 1, parentId = folderId, groupId = groupId).let(nodeMapper::convertDtoToModel),
            null
        )
        val node2 = nodeService.createOrderable(
            FileDtoInput(name = "Node 2", position = 2, parentId = folderId, groupId = groupId).let(nodeMapper::convertDtoToModel),
            null
        )

        val orderedNodes = nodeService.findByParentIdOrderByPosition(folderId)

        assertEquals(3, orderedNodes.size)
        assertEquals("Node 1", orderedNodes[0].name)
        assertEquals("Node 2", orderedNodes[1].name)
        assertEquals("Node 3", orderedNodes[2].name)
    }
}