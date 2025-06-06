package cz.binaryburst.treestructure.root.controller

import cz.binaryburst.generic.controller.BaseController
import cz.binaryburst.treestructure.root.dto.RootDtoInput
import cz.binaryburst.treestructure.root.dto.RootDtoOutput
import cz.binaryburst.treestructure.root.entity.RootEntity
import cz.binaryburst.treestructure.root.mapper.RootMapper
import cz.binaryburst.treestructure.root.model.RootModel
import cz.binaryburst.treestructure.root.repository.RootRepository
import cz.binaryburst.treestructure.root.service.RootService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/roots")
class RootController(
    service: RootService,
    mapper: RootMapper
) : BaseController<
        Long,
        RootDtoInput,
        RootDtoOutput,
        RootModel,
        RootEntity,
        RootMapper,
        RootRepository,
        RootService
        >(
    service = service,
    mapper = mapper
) {

    @GetMapping
    override fun getAll(pageable: Pageable) = super.getAll(pageable)

    @GetMapping("/{id}")
    override fun get(@PathVariable id: Long) = super.get(id)

    @PostMapping
    override fun create(@RequestBody dto: RootDtoInput) = super.create(dto)

    @PutMapping("/{id}")
    override fun update(@PathVariable id: Long, @RequestBody dto: RootDtoInput) = super.update(id, dto)

    @DeleteMapping("/{id}")
    override fun delete(@PathVariable id: Long) = super.delete(id)

    @PostMapping("/batch")
    override fun addAll(@RequestBody dtos: List<RootDtoInput>) = super.addAll(dtos)

    @PutMapping("/batch")
    override fun updateAll(@RequestBody dtos: List<RootDtoInput>) = super.updateAll(dtos)

    @DeleteMapping
    override fun deleteAll() = super.deleteAll()
}