package cz.binaryburst.treestructure.group.controller

import cz.binaryburst.generic.controller.BaseController
import cz.binaryburst.generic.dto.PageResponse
import cz.binaryburst.treestructure.group.dto.GroupDtoInput
import cz.binaryburst.treestructure.group.dto.GroupDtoOutput
import cz.binaryburst.treestructure.group.dto.GroupParams
import cz.binaryburst.treestructure.group.entity.GroupEntity
import cz.binaryburst.treestructure.group.mapper.GroupMapper
import cz.binaryburst.treestructure.group.model.GroupModel
import cz.binaryburst.treestructure.group.repository.GroupRepository
import cz.binaryburst.treestructure.group.service.GroupService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/groups")
class GroupController(
    private val groupService: GroupService,
    private val groupMapper: GroupMapper
) : BaseController<
        Long,
        GroupDtoInput,
        GroupDtoOutput,
        GroupModel,
        GroupEntity,
        GroupMapper,
        GroupRepository,
        GroupService
        >(
    service = groupService,
    mapper = groupMapper
) {

    @GetMapping
    override fun getAll(pageable: Pageable) = super.getAll(pageable)

    @GetMapping("/{id}")
    override fun get(@PathVariable id: Long) = super.get(id)

    @PostMapping
    override fun create(@RequestBody dto: GroupDtoInput) = super.create(dto)

    @PutMapping("/{id}")
    override fun update(@PathVariable id: Long, @RequestBody dto: GroupDtoInput) = super.update(id, dto)

    @DeleteMapping("/{id}")
    override fun delete(@PathVariable id: Long) = super.delete(id)

    @PostMapping("/batch")
    override fun addAll(@RequestBody dtos: List<GroupDtoInput>) = super.addAll(dtos)

    @PutMapping("/batch")
    override fun updateAll(@RequestBody dtos: List<GroupDtoInput>) = super.updateAll(dtos)

    @DeleteMapping
    override fun deleteAll() = super.deleteAll()

    // Orderable endpoints
    @GetMapping("/orderable")
    fun getAllOrderable(
        pageable: Pageable,
        @RequestParam(required = false) zoneId: Long?
    ): ResponseEntity<PageResponse<GroupDtoOutput>> {
        val params = if (zoneId != null) GroupParams(zoneId) else null
        val pageResponse = groupService.findAllOrderable(pageable, params)
        val dtoPageResponse = PageResponse(
            content = pageResponse.content.map(groupMapper::convertModelToDtoOut),
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
        @RequestBody dto: GroupDtoInput,
        @RequestParam(required = false) zoneId: Long?
    ): ResponseEntity<GroupDtoOutput> {
        val params = if (zoneId != null) GroupParams(zoneId) else null
        val model = groupMapper.convertDtoToModel(dto)
        val createdModel = groupService.createOrderable(model, params)
        val createdDto = groupMapper.convertModelToDtoOut(createdModel)
        return ResponseEntity(createdDto, HttpStatus.CREATED)
    }

    @DeleteMapping("/orderable/{id}")
    fun deleteOrderableById(
        @PathVariable id: Long,
        @RequestParam(required = false) zoneId: Long?
    ): ResponseEntity<Unit> {
        val params = if (zoneId != null) GroupParams(zoneId) else null
        groupService.deleteOrderableById(id, params)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/orderable/batch")
    fun addAllOrderable(
        @RequestBody dtos: List<GroupDtoInput>,
        @RequestParam(required = false) zoneId: Long?
    ): ResponseEntity<List<GroupDtoOutput>> {
        val params = if (zoneId != null) GroupParams(zoneId) else null
        val models = dtos.map(groupMapper::convertDtoToModel)
        val createdModels = groupService.addAllOrderable(models, params)
        val createdDtos = createdModels.map(groupMapper::convertModelToDtoOut)
        return ResponseEntity(createdDtos, HttpStatus.CREATED)
    }

    @PutMapping("/orderable/reorder")
    fun reorder(
        @RequestBody dto: GroupDtoInput,
        @RequestParam(required = false) zoneId: Long?
    ): ResponseEntity<GroupDtoOutput> {
        val params = if (zoneId != null) GroupParams(zoneId) else null
        val model = groupMapper.convertDtoToModel(dto)
        groupService.reorder(model, params)
        val updatedModel = groupService.findById(model.id)
        val updatedDto = groupMapper.convertModelToDtoOut(updatedModel)
        return ResponseEntity(updatedDto, HttpStatus.OK)
    }

    @DeleteMapping("/orderable")
    fun deleteAllOrderable(@RequestParam(required = false) zoneId: Long?): ResponseEntity<Unit> {
        val params = if (zoneId != null) GroupParams(zoneId) else null
        groupService.deleteOrderableAll(params)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    // Legacy endpoints
    @GetMapping("/by-zone/{zoneId}")
    fun getByZoneId(@PathVariable zoneId: Long): ResponseEntity<List<GroupDtoOutput>> {
        val groups = groupService.findByZoneId(zoneId)
        val groupDtos = groups.map { groupMapper.convertModelToDtoOut(it) }
        return ResponseEntity.ok(groupDtos)
    }

    @GetMapping("/by-zone/{zoneId}/ordered")
    fun getByZoneIdOrderByPosition(@PathVariable zoneId: Long): ResponseEntity<List<GroupDtoOutput>> {
        val groups = groupService.findByZoneIdOrderByPosition(zoneId)
        val groupDtos = groups.map { groupMapper.convertModelToDtoOut(it) }
        return ResponseEntity.ok(groupDtos)
    }
}