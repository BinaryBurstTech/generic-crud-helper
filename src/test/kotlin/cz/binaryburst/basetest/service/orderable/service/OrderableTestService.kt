package cz.binaryburst.basetest.service.orderable.service


import cz.binaryburst.basetest.service.orderable.dto.OrderableTestDtoInput
import cz.binaryburst.basetest.service.orderable.dto.OrderableTestDtoOutput
import cz.binaryburst.basetest.service.orderable.entity.OrderableTestEntity
import cz.binaryburst.basetest.service.orderable.mapper.OrderableTestMapper
import cz.binaryburst.basetest.service.orderable.model.OrderableTestModel
import cz.binaryburst.basetest.service.orderable.repository.OrderableTestRepository
import cz.binaryburst.generic.service.OrderableService
import org.springframework.stereotype.Service

@Service
class OrderableTestService(
    repository: OrderableTestRepository,
    mapper: OrderableTestMapper
) : OrderableService<
        Long,
        OrderableTestDtoInput,
        OrderableTestDtoOutput,
        OrderableTestModel,
        OrderableTestEntity,
        OrderableTestRepository,
        OrderableTestMapper
        >(
    repository = repository,
    mapper = mapper
)