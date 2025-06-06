package cz.binaryburst.treestructure.zone.service

import cz.binaryburst.generic.service.BaseService
import cz.binaryburst.treestructure.zone.dto.ZoneDtoInput
import cz.binaryburst.treestructure.zone.dto.ZoneDtoOutput
import cz.binaryburst.treestructure.zone.entity.ZoneEntity
import cz.binaryburst.treestructure.zone.mapper.ZoneMapper
import cz.binaryburst.treestructure.zone.model.ZoneModel
import cz.binaryburst.treestructure.zone.repository.ZoneRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ZoneService(
    repository: ZoneRepository,
    mapper: ZoneMapper
) : BaseService<
        Long,
        ZoneDtoInput,
        ZoneDtoOutput,
        ZoneModel,
        ZoneEntity,
        ZoneRepository,
        ZoneMapper
        >(
    repository = repository,
    mapper = mapper
) {

    @Transactional(readOnly = true)
    fun findByRootId(rootId: Long): List<ZoneModel> {
        return repository.findByRootId(rootId).map { mapper.convertEntityToModel(it) }
    }
}