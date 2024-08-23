package cz.binaryburst.basetest.service.embeddable.service


import cz.binaryburst.basetest.service.embeddable.dto.EmbeddedTestDtoInput
import cz.binaryburst.basetest.service.embeddable.dto.EmbeddedTestDtoOutput
import cz.binaryburst.basetest.service.embeddable.entity.EmbeddedTestEntity
import cz.binaryburst.basetest.service.embeddable.mapper.EmbeddedTestMapper
import cz.binaryburst.basetest.service.embeddable.model.EmbeddedTestModel
import cz.binaryburst.basetest.service.embeddable.repository.EmbeddedTestRepository
import cz.binaryburst.generic.service.BaseService
import org.springframework.stereotype.Service

@Service
class EmbeddedTestService(
    repository: EmbeddedTestRepository,
    mapper: EmbeddedTestMapper
) : BaseService<
        Long,
        EmbeddedTestDtoInput,
        EmbeddedTestDtoOutput,
        EmbeddedTestModel,
        EmbeddedTestEntity,
        EmbeddedTestRepository,
        EmbeddedTestMapper
        >(
    repository = repository,
    mapper = mapper
)