package cz.binaryburst.basetest.service.orderable.repository

import cz.binaryburst.basetest.service.orderable.entity.OrderableTestEntity
import cz.binaryburst.generic.repository.OrderableRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderableTestRepository : OrderableRepository<OrderableTestEntity, Long>