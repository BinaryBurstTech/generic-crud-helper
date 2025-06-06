package cz.binaryburst.treestructure.node.controller

import cz.binaryburst.generic.controller.OrderableController
import cz.binaryburst.generic.dto.PageResponse
import cz.binaryburst.treestructure.node.dto.NodeDtoInput
import cz.binaryburst.treestructure.node.dto.NodeDtoOutput
import cz.binaryburst.treestructure.node.dto.NodeParams
import cz.binaryburst.treestructure.node.entity.NodeEntity
import cz.binaryburst.treestructure.node.mapper.NodeMapper
import cz.binaryburst.treestructure.node.model.NodeModel
import cz.binaryburst.treestructure.node.repository.NodeRepository
import cz.binaryburst.treestructure.node.service.NodeService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/nodes")
class NodeController(
    private val nodeService: NodeService,
    private val nodeMapper: NodeMapper
) : OrderableController<
        Long,
        NodeParams,
        NodeDtoInput,
        NodeDtoOutput,
        NodeModel,
        NodeEntity,
        NodeMapper,
        NodeRepository,
        NodeService
        >(
    orderableService = nodeService,
    mapper = nodeMapper
) {

    // Base CRUD endpoints
    @GetMapping
    override fun getAll(pageable: Pageable) = super.getAll(pageable)

    @GetMapping("/{id}")
    override fun get(@PathVariable id: Long) = super.get(id)

    @PostMapping
    override fun create(@RequestBody dto: NodeDtoInput) = super.create(dto)

    @PutMapping("/{id}")
    override fun update(@PathVariable id: Long, @RequestBody dto: NodeDtoInput) = super.update(id, dto)

    @DeleteMapping("/{id}")
    override fun delete(@PathVariable id: Long) = super.delete(id)

    @PostMapping("/batch")
    override fun addAll(@RequestBody dtos: List<NodeDtoInput>) = super.addAll(dtos)

    @PutMapping("/batch")
    override fun updateAll(@RequestBody dtos: List<NodeDtoInput>) = super.updateAll(dtos)

    @DeleteMapping
    override fun deleteAll() = super.deleteAll()

    // Orderable endpoints
    @GetMapping("/orderable")
    fun getAllOrderable(
        pageable: Pageable,
        @RequestParam(required = false) groupId: Long?,
        @RequestParam(required = false) parentId: Long?
    ): ResponseEntity<PageResponse<NodeDtoOutput>> {
        val params = NodeParams(groupId, parentId)
        return getAllOrderable(pageable, params)
    }

    @PostMapping("/orderable")
    fun createOrderable(
        @RequestBody dto: NodeDtoInput,
        @RequestParam(required = false) groupId: Long?,
        @RequestParam(required = false) parentId: Long?
    ): ResponseEntity<NodeDtoOutput> {
        val params = NodeParams(groupId ?: dto.groupId, parentId ?: dto.parentId)
        return createOrderable(dto, params)
    }

    @DeleteMapping("/orderable/{id}")
    fun deleteOrderableById(
        @PathVariable id: Long,
        @RequestParam(required = false) groupId: Long?,
        @RequestParam(required = false) parentId: Long?
    ): ResponseEntity<Unit> {
        val params = NodeParams(groupId, parentId)
        return deleteOrderableById(id, params)
    }

    @PostMapping("/orderable/batch")
    fun addAllOrderable(
        @RequestBody dtos: List<NodeDtoInput>,
        @RequestParam(required = false) groupId: Long?,
        @RequestParam(required = false) parentId: Long?
    ): ResponseEntity<List<NodeDtoOutput>> {
        val params = NodeParams(groupId, parentId)
        return addAllOrderable(dtos, params)
    }

    @PutMapping("/orderable/reorder")
    fun reorder(
        @RequestBody dto: NodeDtoInput,
        @RequestParam(required = false) groupId: Long?,
        @RequestParam(required = false) parentId: Long?
    ): ResponseEntity<NodeDtoOutput> {
        val params = NodeParams(groupId ?: dto.groupId, parentId ?: dto.parentId)
        return reorder(dto, params)
    }

    @DeleteMapping("/orderable")
    fun deleteAllOrderable(
        @RequestParam(required = false) groupId: Long?,
        @RequestParam(required = false) parentId: Long?
    ): ResponseEntity<Unit> {
        val params = NodeParams(groupId, parentId)
        return deleteAllOrderable(params)
    }

    // Legacy endpoints with ordering
    @GetMapping("/by-group/{groupId}")
    fun getByGroupId(@PathVariable groupId: Long): ResponseEntity<List<NodeDtoOutput>> {
        val nodes = nodeService.findByGroupId(groupId)
        val nodeDtos = nodes.map { nodeMapper.convertModelToDtoOut(it) }
        return ResponseEntity.ok(nodeDtos)
    }

    @GetMapping("/by-group/{groupId}/ordered")
    fun getByGroupIdOrderByPosition(@PathVariable groupId: Long): ResponseEntity<List<NodeDtoOutput>> {
        val nodes = nodeService.findByGroupIdOrderByPosition(groupId)
        val nodeDtos = nodes.map { nodeMapper.convertModelToDtoOut(it) }
        return ResponseEntity.ok(nodeDtos)
    }

    @GetMapping("/by-parent/{parentId}")
    fun getByParentId(@PathVariable parentId: Long): ResponseEntity<List<NodeDtoOutput>> {
        val nodes = nodeService.findByParentId(parentId)
        val nodeDtos = nodes.map { nodeMapper.convertModelToDtoOut(it) }
        return ResponseEntity.ok(nodeDtos)
    }

    @GetMapping("/by-parent/{parentId}/ordered")
    fun getByParentIdOrderByPosition(@PathVariable parentId: Long): ResponseEntity<List<NodeDtoOutput>> {
        val nodes = nodeService.findByParentIdOrderByPosition(parentId)
        val nodeDtos = nodes.map { nodeMapper.convertModelToDtoOut(it) }
        return ResponseEntity.ok(nodeDtos)
    }

    @GetMapping("/roots/by-group/{groupId}")
    fun getRootNodesByGroupId(@PathVariable groupId: Long): ResponseEntity<List<NodeDtoOutput>> {
        val nodes = nodeService.findRootNodesByGroupId(groupId)
        val nodeDtos = nodes.map { nodeMapper.convertModelToDtoOut(it) }
        return ResponseEntity.ok(nodeDtos)
    }

    @GetMapping("/roots/by-group/{groupId}/ordered")
    fun getRootNodesByGroupIdOrderByPosition(@PathVariable groupId: Long): ResponseEntity<List<NodeDtoOutput>> {
        val nodes = nodeService.findRootNodesByGroupIdOrderByPosition(groupId)
        val nodeDtos = nodes.map { nodeMapper.convertModelToDtoOut(it) }
        return ResponseEntity.ok(nodeDtos)
    }

    @GetMapping("/by-group/{groupId}/name/{name}")
    fun getByGroupIdAndName(
        @PathVariable groupId: Long,
        @PathVariable name: String
    ): ResponseEntity<List<NodeDtoOutput>> {
        val nodes = nodeService.findByGroupIdAndName(groupId, name)
        val nodeDtos = nodes.map { nodeMapper.convertModelToDtoOut(it) }
        return ResponseEntity.ok(nodeDtos)
    }
}