package cz.binaryburst.treestructure.group.controller

import cz.binaryburst.generic.controller.OrderableController
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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/groups")
class GroupController(
    private val groupService: GroupService,
    private val groupMapper: GroupMapper
) : OrderableController<
        Long,
        GroupParams,
        GroupDtoInput,
        GroupDtoOutput,
        GroupModel,
        GroupEntity,
        GroupMapper,
        GroupRepository,
        GroupService
        >(
    orderableService = groupService,
    mapper = groupMapper
) {

    // Base CRUD endpoints
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
        return getAllOrderable(pageable, params)
    }

    @PostMapping("/orderable")
    fun createOrderable(
        @RequestBody dto: GroupDtoInput,
        @RequestParam(required = false) zoneId: Long?
    ): ResponseEntity<GroupDtoOutput> {
        val params = if (zoneId != null) GroupParams(zoneId) else null
        return createOrderable(dto, params)
    }

    @DeleteMapping("/orderable/{id}")
    fun deleteOrderableById(
        @PathVariable id: Long,
        @RequestParam(required = false) zoneId: Long?
    ): ResponseEntity<Unit> {
        val params = if (zoneId != null) GroupParams(zoneId) else null
        return deleteOrderableById(id, params)
    }

    @PostMapping("/orderable/batch")
    fun addAllOrderable(
        @RequestBody dtos: List<GroupDtoInput>,
        @RequestParam(required = false) zoneId: Long?
    ): ResponseEntity<List<GroupDtoOutput>> {
        val params = if (zoneId != null) GroupParams(zoneId) else null
        return addAllOrderable(dtos, params)
    }

    @PutMapping("/orderable/reorder")
    fun reorder(
        @RequestBody dto: GroupDtoInput,
        @RequestParam(required = false) zoneId: Long?
    ): ResponseEntity<GroupDtoOutput> {
        val params = if (zoneId != null) GroupParams(zoneId) else null
        return reorder(dto, params)
    }

    @DeleteMapping("/orderable")
    fun deleteAllOrderable(@RequestParam(required = false) zoneId: Long?): ResponseEntity<Unit> {
        val params = if (zoneId != null) GroupParams(zoneId) else null
        return deleteAllOrderable(params)
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