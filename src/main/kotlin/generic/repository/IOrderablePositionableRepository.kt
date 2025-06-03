package cz.binaryburst.generic.repository

import jakarta.persistence.EntityManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

/**
 * Abstract repository class providing positional ordering functionality for entities.
 *
 * @param PARAMS The type of optional parameters for filtering.
 * @param ENTITY The type of the entity.
 */
abstract class IOrderablePositionableRepository<PARAMS, ENTITY>(
    val entityManager: EntityManager,
    private val entityClass: Class<ENTITY>
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Increments positions of entities in a given range.
     *
     * @param newPosition The new position (lower bound).
     * @param oldPosition The old position (upper bound).
     * @param params Optional parameters for filtering.
     */
    open fun incrementPositions(newPosition: Int, oldPosition: Int, params: PARAMS?) {
        logger.debug("incrementPositions: newPosition={}, oldPosition={}, params={}", newPosition, oldPosition, params)
        entityManager.createQuery(
            "UPDATE ${entityClass.simpleName} e SET e.position = e.position + 1 " +
                    "WHERE e.position >= :newPosition AND e.position < :oldPosition"
        ).setParameter("newPosition", newPosition)
            .setParameter("oldPosition", oldPosition)
            .executeUpdate()
    }

    /**
     * Decrements positions of entities in a given range.
     *
     * @param oldPosition The old position (lower bound).
     * @param newPosition The new position (upper bound).
     * @param params Optional parameters for filtering.
     */
    open fun decrementPositions(oldPosition: Int, newPosition: Int, params: PARAMS?) {
        logger.debug("decrementPositions: oldPosition={}, newPosition={}, params={}", oldPosition, newPosition, params)
        entityManager.createQuery(
            "UPDATE ${entityClass.simpleName} e SET e.position = e.position - 1 " +
                    "WHERE e.position > :oldPosition AND e.position <= :newPosition"
        ).setParameter("oldPosition", oldPosition)
            .setParameter("newPosition", newPosition)
            .executeUpdate()
    }

    /**
     * Increments positions of entities from a given position.
     *
     * @param position The position from which to increment.
     * @param params Optional parameters for filtering.
     */
    open fun incrementPositions(position: Int, params: PARAMS?) {
        logger.debug("incrementPositions (single): position={}, params={}", position, params)
        entityManager.createQuery(
            "UPDATE ${entityClass.simpleName} e SET e.position = e.position + 1 " +
                    "WHERE e.position >= :position"
        ).setParameter("position", position)
            .executeUpdate()
    }

    /**
     * Decrements positions of entities after a given position.
     *
     * @param position The position after which to decrement.
     * @param params Optional parameters for filtering.
     */
    open fun decrementPositions(position: Int, params: PARAMS?) {
        logger.debug("decrementPositions (single): position={}, params={}", position, params)
        entityManager.createQuery(
            "UPDATE ${entityClass.simpleName} e SET e.position = e.position - 1 " +
                    "WHERE e.position > :position"
        ).setParameter("position", position)
            .executeUpdate()
    }

    /**
     * Finds the maximum position value.
     *
     * @param params Optional parameters for filtering.
     * @return The maximum position, or null if no entities exist.
     */
    open fun findMaxPosition(params: PARAMS?): Int? {
        logger.debug("findMaxPosition: params={}", params)
        return entityManager.createQuery(
            "SELECT MAX(e.position) FROM ${entityClass.simpleName} e", Int::class.javaObjectType
        ).resultList.firstOrNull() as Int?
    }

    /**
     * Finds all entities ordered by position.
     *
     * @param params Optional parameters for filtering.
     * @return A list of all entities ordered by position.
     */
    @Deprecated("Use findAllByOrderByPositionAsc(pageable, params) for better performance with large datasets")
    open fun findAllByOrderByPositionAsc(params: PARAMS?): List<ENTITY> {
        logger.debug("findAllByOrderByPositionAsc: params={}", params)
        return entityManager.createQuery(
            "SELECT e FROM ${entityClass.simpleName} e ORDER BY e.position ASC", entityClass
        ).resultList
    }

    /**
     * Finds all entities ordered by position with pagination support.
     *
     * @param pageable Pagination information.
     * @param params Optional parameters for filtering.
     * @return A paginated result of entities ordered by position.
     */
    open fun findAllByOrderByPositionAsc(pageable: Pageable, params: PARAMS?): Page<ENTITY> {
        logger.debug("findAllByOrderByPositionAsc with pagination: pageable={}, params={}", pageable, params)

        // Count query
        val countQuery = entityManager.createQuery(
            "SELECT COUNT(e) FROM ${entityClass.simpleName} e", Long::class.javaObjectType
        )
        val total = countQuery.singleResult

        // If total is 0, return empty page
        if (total == 0L) {
            return PageImpl(emptyList(), pageable, 0)
        }

        // Data query with pagination
        val query = entityManager.createQuery(
            "SELECT e FROM ${entityClass.simpleName} e ORDER BY e.position ASC",
            entityClass
        )

        // Apply pagination
        if (pageable.isPaged) {
            query.firstResult = pageable.offset.toInt()
            query.maxResults = pageable.pageSize
        }

        val content = query.resultList

        return PageImpl(content, pageable, total)
    }

    /**
     * Deletes all entities.
     *
     * @param params Optional parameters for filtering.
     */
    open fun deleteAllByParams(params: PARAMS?) {
        logger.warn("CRITICAL OPERATION: Deleting all entities")
        entityManager.createQuery(
            "DELETE FROM ${entityClass.simpleName}"
        ).executeUpdate()
    }
}