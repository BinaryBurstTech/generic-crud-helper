package cz.binaryburst.treestructure.root.service

import cz.binaryburst.generic.service.BaseService
import cz.binaryburst.treestructure.root.dto.RootDtoInput
import cz.binaryburst.treestructure.root.dto.RootDtoOutput
import cz.binaryburst.treestructure.root.entity.RootEntity
import cz.binaryburst.treestructure.root.mapper.RootMapper
import cz.binaryburst.treestructure.root.model.RootModel
import cz.binaryburst.treestructure.root.repository.RootRepository
import org.springframework.stereotype.Service

@Service
class RootService(
    repository: RootRepository,
    mapper: RootMapper
) : BaseService<
        Long,
        RootDtoInput,
        RootDtoOutput,
        RootModel,
        RootEntity,
        RootRepository,
        RootMapper
        >(
    repository = repository,
    mapper = mapper
)