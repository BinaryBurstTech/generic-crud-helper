package cz.binaryburst.basetest.service.orderable.service

import cz.binaryburst.basetest.service.orderable.entity.OrderableTestEntity
import cz.binaryburst.generic.repository.IOrderablePositionableRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class OrderablePositionableRepository(
    entityManager: EntityManager
) : IOrderablePositionableRepository<OrderableParamsTest, OrderableTestEntity>(
    entityManager = entityManager,
    OrderableTestEntity::class.java
)