package cz.binaryburst.basetest.service.service

import cz.binaryburst.basetest.service.dto.BaseTestInsertDto
import cz.binaryburst.basetest.service.dto.BaseTestOutputDto
import cz.binaryburst.basetest.service.entity.BaseTestEntity
import cz.binaryburst.basetest.service.mapper.BaseTestMapper
import cz.binaryburst.basetest.service.model.BaseTestModel
import cz.binaryburst.basetest.service.repository.BaseTestRepository
import cz.binaryburst.generic.service.BaseService
import org.springframework.stereotype.Service

@Service
class BaseTestService(
    repository: BaseTestRepository,
    mapper: BaseTestMapper
) : BaseService<
        Long,
        BaseTestInsertDto,
        BaseTestOutputDto,
        BaseTestModel,
        BaseTestEntity,
        BaseTestRepository,
        BaseTestMapper
        >(
    repository = repository,
    mapper = mapper
)