package cz.binaryburst.generic.repository

import jakarta.persistence.EntityManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class IOrderablePositionableRepository<PARAMS, ENTITY>(
    private val entityManager: EntityManager,
    private val entityClass: Class<ENTITY>
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    open fun incrementPositions(newPosition: Int, oldPosition: Int, params: PARAMS?) {
        logger.debug("incrementPositions: newPosition={}, oldPosition={}, params={}", newPosition, oldPosition, params)
        entityManager.createQuery(
            "UPDATE ${entityClass.simpleName} e SET e.position = e.position + 1 " +
                    "WHERE e.position >= :newPosition AND e.position < :oldPosition"
        ).setParameter("newPosition", newPosition)
            .setParameter("oldPosition", oldPosition)
            .executeUpdate()
    }

    open fun decrementPositions(oldPosition: Int, newPosition: Int, params: PARAMS?) {
        logger.debug("decrementPositions: oldPosition={}, newPosition={}, params={}", oldPosition, newPosition, params)
        entityManager.createQuery(
            "UPDATE ${entityClass.simpleName} e SET e.position = e.position - 1 " +
                    "WHERE e.position > :oldPosition AND e.position <= :newPosition"
        ).setParameter("oldPosition", oldPosition)
            .setParameter("newPosition", newPosition)
            .executeUpdate()
    }

    open fun incrementPositions(position: Int, params: PARAMS?) {
        logger.debug("incrementPositions (single): position={}, params={}", position, params)
        entityManager.createQuery(
            "UPDATE ${entityClass.simpleName} e SET e.position = e.position + 1 " +
                    "WHERE e.position >= :position"
        ).setParameter("position", position)
            .executeUpdate()
    }

    open fun decrementPositions(position: Int, params: PARAMS?) {
        logger.debug("decrementPositions (single): position={}, params={}", position, params)
        entityManager.createQuery(
            "UPDATE ${entityClass.simpleName} e SET e.position = e.position - 1 " +
                    "WHERE e.position > :position"
        ).setParameter("position", position)
            .executeUpdate()
    }

    open fun findMaxPosition(params: PARAMS?): Int? {
        logger.debug("findMaxPosition: params={}", params)
        return entityManager.createQuery(
            "SELECT MAX(e.position) FROM ${entityClass.simpleName} e", Int::class.javaObjectType
        ).singleResult
    }

    open fun findAllByOrderByPositionAsc(params: PARAMS?): List<ENTITY> {
        logger.debug("findAllByOrderByPositionAsc: params={}", params)
        return entityManager.createQuery(
            "SELECT e FROM ${entityClass.simpleName} e ORDER BY e.position ASC", entityClass
        ).resultList
    }

    open fun deleteAllByParams(params: PARAMS?) {
        entityManager.createQuery(
            "DELETE FROM ${entityClass.simpleName}"
        ).executeUpdate()
    }
}