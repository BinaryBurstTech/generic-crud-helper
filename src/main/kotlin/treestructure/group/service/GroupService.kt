package cz.binaryburst.treestructure.group.service

import cz.binaryburst.generic.service.OrderableService
import cz.binaryburst.treestructure.group.dto.GroupDtoInput
import cz.binaryburst.treestructure.group.dto.GroupDtoOutput
import cz.binaryburst.treestructure.group.dto.GroupParams
import cz.binaryburst.treestructure.group.entity.GroupEntity
import cz.binaryburst.treestructure.group.mapper.GroupMapper
import cz.binaryburst.treestructure.group.model.GroupModel
import cz.binaryburst.treestructure.group.repository.GroupPositionableRepository
import cz.binaryburst.treestructure.group.repository.GroupRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupService(
    repository: GroupRepository,
    mapper: GroupMapper,
    positionableRepository: GroupPositionableRepository
) : OrderableService<
        Long,
        GroupParams,
        GroupDtoInput,
        GroupDtoOutput,
        GroupModel,
        GroupEntity,
        GroupRepository,
        GroupMapper
        >(
    repository = repository,
    mapper = mapper,
    positionableRepository = positionableRepository
) {

    @Transactional(readOnly = true)
    fun findByZoneId(zoneId: Long): List<GroupModel> {
        return repository.findByZoneId(zoneId).map { mapper.convertEntityToModel(it) }
    }

    @Transactional(readOnly = true)
    fun findByZoneIdOrderByPosition(zoneId: Long): List<GroupModel> {
        return repository.findByZoneIdOrderByPosition(zoneId).map { mapper.convertEntityToModel(it) }
    }
}