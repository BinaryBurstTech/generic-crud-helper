package cz.binaryburst.basetest.service.base.service

import cz.binaryburst.basetest.service.base.dto.BaseTestDtoInput
import cz.binaryburst.basetest.service.base.dto.BaseTestDtoOutput
import cz.binaryburst.basetest.service.base.entity.BaseTestEntity
import cz.binaryburst.basetest.service.base.mapper.BaseTestMapper
import cz.binaryburst.basetest.service.base.model.BaseTestModel
import cz.binaryburst.basetest.service.base.repository.BaseTestRepository
import cz.binaryburst.generic.service.BaseService
import org.springframework.stereotype.Service

@Service
class BaseTestService(
    repository: BaseTestRepository,
    mapper: BaseTestMapper
) : BaseService<
        Long,
        BaseTestDtoInput,
        BaseTestDtoOutput,
        BaseTestModel,
        BaseTestEntity,
        BaseTestRepository,
        BaseTestMapper
        >(
    repository = repository,
    mapper = mapper
)