package cz.binaryburst.treestructure.zone.controller

import cz.binaryburst.generic.controller.BaseController
import cz.binaryburst.treestructure.zone.dto.ZoneDtoInput
import cz.binaryburst.treestructure.zone.dto.ZoneDtoOutput
import cz.binaryburst.treestructure.zone.entity.ZoneEntity
import cz.binaryburst.treestructure.zone.mapper.ZoneMapper
import cz.binaryburst.treestructure.zone.model.ZoneModel
import cz.binaryburst.treestructure.zone.repository.ZoneRepository
import cz.binaryburst.treestructure.zone.service.ZoneService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/zones")
class ZoneController(
    private val zoneService: ZoneService,
    mapper: ZoneMapper
) : BaseController<
        Long,
        ZoneDtoInput,
        ZoneDtoOutput,
        ZoneModel,
        ZoneEntity,
        ZoneMapper,
        ZoneRepository,
        ZoneService
        >(
    service = zoneService,
    mapper = mapper
) {

    @GetMapping
    override fun getAll(pageable: Pageable) = super.getAll(pageable)

    @GetMapping("/{id}")
    override fun get(@PathVariable id: Long) = super.get(id)

    @PostMapping
    override fun create(@RequestBody dto: ZoneDtoInput) = super.create(dto)

    @PutMapping("/{id}")
    override fun update(@PathVariable id: Long, @RequestBody dto: ZoneDtoInput) = super.update(id, dto)

    @DeleteMapping("/{id}")
    override fun delete(@PathVariable id: Long) = super.delete(id)

    @PostMapping("/batch")
    override fun addAll(@RequestBody dtos: List<ZoneDtoInput>) = super.addAll(dtos)

    @PutMapping("/batch")
    override fun updateAll(@RequestBody dtos: List<ZoneDtoInput>) = super.updateAll(dtos)

    @DeleteMapping
    override fun deleteAll() = super.deleteAll()

    @GetMapping("/by-root/{rootId}")
    fun getByRootId(@PathVariable rootId: Long): ResponseEntity<List<ZoneDtoOutput>> {
        val zones = zoneService.findByRootId(rootId)
        val zoneDtos = zones.map { mapper.convertModelToDtoOut(it) }
        return ResponseEntity.ok(zoneDtos)
    }
}