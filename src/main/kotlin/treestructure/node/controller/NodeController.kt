package cz.binaryburst.treestructure.node.controller

import cz.binaryburst.generic.controller.BaseController
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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/nodes")
class NodeController(
    private val nodeService: NodeService,
    private val nodeMapper: NodeMapper
) : BaseController<
        Long,
        NodeDtoInput,
        NodeDtoOutput,
        NodeModel,
        NodeEntity,
        NodeMapper,
        NodeRepository,
        NodeService
        >(
    service = nodeService,
    mapper = nodeMapper
) {

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
        val pageResponse = nodeService.findAllOrderable(pageable, params)
        val dtoPageResponse = PageResponse(
            content = pageResponse.content.map(nodeMapper::convertModelToDtoOut),
            totalElements = pageResponse.totalElements,
            totalPages = pageResponse.totalPages,
            pageNumber = pageResponse.pageNumber,
            pageSize = pageResponse.pageSize,
            isLast = pageResponse.isLast
        )
        return ResponseEntity(dtoPageResponse, HttpStatus.OK)
    }

    @PostMapping("/orderable")
    fun createOrderable(
        @RequestBody dto: NodeDtoInput,
        @RequestParam(required = false) groupId: Long?,
        @RequestParam(required = false) parentId: Long?
    ): ResponseEntity<NodeDtoOutput> {
        val params = NodeParams(groupId ?: dto.groupId, parentId ?: dto.parentId)
        val model = nodeMapper.convertDtoToModel(dto)
        val createdModel = nodeService.createOrderable(model, params)
        val createdDto = nodeMapper.convertModelToDtoOut(createdModel)
        return ResponseEntity(createdDto, HttpStatus.CREATED)
    }

    @DeleteMapping("/orderable/{id}")
    fun deleteOrderableById(
        @PathVariable id: Long,
        @RequestParam(required = false) groupId: Long?,
        @RequestParam(required = false) parentId: Long?
    ): ResponseEntity<Unit> {
        val params = NodeParams(groupId, parentId)
        nodeService.deleteOrderableById(id, params)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/orderable/batch")
    fun addAllOrderable(
        @RequestBody dtos: List<NodeDtoInput>,
        @RequestParam(required = false) groupId: Long?,
        @RequestParam(required = false) parentId: Long?
    ): ResponseEntity<List<NodeDtoOutput>> {
        val params = NodeParams(groupId, parentId)
        val models = dtos.map(nodeMapper::convertDtoToModel)
        val createdModels = nodeService.addAllOrderable(models, params)
        val createdDtos = createdModels.map(nodeMapper::convertModelToDtoOut)
        return ResponseEntity(createdDtos, HttpStatus.CREATED)
    }

    @PutMapping("/orderable/reorder")
    fun reorder(
        @RequestBody dto: NodeDtoInput,
        @RequestParam(required = false) groupId: Long?,
        @RequestParam(required = false) parentId: Long?
    ): ResponseEntity<NodeDtoOutput> {
        val params = NodeParams(groupId ?: dto.groupId, parentId ?: dto.parentId)
        val model = nodeMapper.convertDtoToModel(dto)
        nodeService.reorder(model, params)
        val updatedModel = nodeService.findById(model.id)
        val updatedDto = nodeMapper.convertModelToDtoOut(updatedModel)
        return ResponseEntity(updatedDto, HttpStatus.OK)
    }

    @DeleteMapping("/orderable")
    fun deleteAllOrderable(
        @RequestParam(required = false) groupId: Long?,
        @RequestParam(required = false) parentId: Long?
    ): ResponseEntity<Unit> {
        val params = NodeParams(groupId, parentId)
        nodeService.deleteOrderableAll(params)
        return ResponseEntity(HttpStatus.NO_CONTENT)
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