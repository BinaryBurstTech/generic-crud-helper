package cz.binaryburst.treestructure.node.service

import cz.binaryburst.generic.service.OrderableService
import cz.binaryburst.treestructure.node.dto.NodeDtoInput
import cz.binaryburst.treestructure.node.dto.NodeDtoOutput
import cz.binaryburst.treestructure.node.dto.NodeParams
import cz.binaryburst.treestructure.node.entity.NodeEntity
import cz.binaryburst.treestructure.node.mapper.NodeMapper
import cz.binaryburst.treestructure.node.model.NodeModel
import cz.binaryburst.treestructure.node.repository.NodePositionableRepository
import cz.binaryburst.treestructure.node.repository.NodeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NodeService(
    repository: NodeRepository,
    mapper: NodeMapper,
    positionableRepository: NodePositionableRepository
) : OrderableService<
        Long,
        NodeParams,
        NodeDtoInput,
        NodeDtoOutput,
        NodeModel,
        NodeEntity,
        NodeRepository,
        NodeMapper
        >(
    repository = repository,
    mapper = mapper,
    positionableRepository = positionableRepository
) {

    @Transactional(readOnly = true)
    fun findByGroupId(groupId: Long): List<NodeModel> {
        return repository.findByGroupId(groupId).map { mapper.convertEntityToModel(it) }
    }

    @Transactional(readOnly = true)
    fun findByGroupIdOrderByPosition(groupId: Long): List<NodeModel> {
        return repository.findByGroupIdOrderByPosition(groupId).map { mapper.convertEntityToModel(it) }
    }

    @Transactional(readOnly = true)
    fun findByParentId(parentId: Long): List<NodeModel> {
        return repository.findByParentId(parentId).map { mapper.convertEntityToModel(it) }
    }

    @Transactional(readOnly = true)
    fun findByParentIdOrderByPosition(parentId: Long): List<NodeModel> {
        return repository.findByParentIdOrderByPosition(parentId).map { mapper.convertEntityToModel(it) }
    }

    @Transactional(readOnly = true)
    fun findRootNodesByGroupId(groupId: Long): List<NodeModel> {
        return repository.findRootNodesByGroupId(groupId).map { mapper.convertEntityToModel(it) }
    }

    @Transactional(readOnly = true)
    fun findRootNodesByGroupIdOrderByPosition(groupId: Long): List<NodeModel> {
        return repository.findRootNodesByGroupIdOrderByPosition(groupId).map { mapper.convertEntityToModel(it) }
    }

    @Transactional(readOnly = true)
    fun findByGroupIdAndName(groupId: Long, name: String): List<NodeModel> {
        return repository.findByGroupIdAndName(groupId, name).map { mapper.convertEntityToModel(it) }
    }
}