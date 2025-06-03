package cz.binaryburst.treestructure.node.repository

import cz.binaryburst.generic.repository.IOrderablePositionableRepository
import cz.binaryburst.treestructure.node.dto.NodeParams
import cz.binaryburst.treestructure.node.entity.NodeEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class NodePositionableRepository(
    entityManager: EntityManager
) : IOrderablePositionableRepository<NodeParams, NodeEntity>(
    entityManager = entityManager,
    NodeEntity::class.java
) {

    private fun buildAdditionalConditions(params: NodeParams?): String {
        val conditions = mutableListOf<String>()

        params?.groupId?.let { conditions.add("e.group.id = :groupId") }
        params?.parentId?.let {
            conditions.add("e.parent.id = :parentId")
        } ?: conditions.add("e.parent IS NULL")

        return if (conditions.isNotEmpty()) {
            " AND " + conditions.joinToString(" AND ")
        } else ""
    }

    private fun buildWhereClause(params: NodeParams?): String {
        val conditions = mutableListOf<String>()

        params?.groupId?.let { conditions.add("e.group.id = :groupId") }
        params?.parentId?.let {
            conditions.add("e.parent.id = :parentId")
        } ?: conditions.add("e.parent IS NULL")

        return if (conditions.isNotEmpty()) {
            " WHERE " + conditions.joinToString(" AND ")
        } else ""
    }

    private fun setParameters(query: Query, params: NodeParams?) {
        params?.groupId?.let { query.setParameter("groupId", it) }
        params?.parentId?.let { query.setParameter("parentId", it) }
    }

    override fun incrementPositions(newPosition: Int, oldPosition: Int, params: NodeParams?) {
        val additionalConditions = buildAdditionalConditions(params)
        val queryString = "UPDATE NodeEntity e SET e.position = e.position + 1 WHERE e.position >= :newPosition AND e.position < :oldPosition$additionalConditions"

        val query = entityManager.createQuery(queryString)
            .setParameter("newPosition", newPosition)
            .setParameter("oldPosition", oldPosition)

        setParameters(query, params)
        query.executeUpdate()
    }

    override fun decrementPositions(oldPosition: Int, newPosition: Int, params: NodeParams?) {
        val additionalConditions = buildAdditionalConditions(params)
        val queryString = "UPDATE NodeEntity e SET e.position = e.position - 1 WHERE e.position > :oldPosition AND e.position <= :newPosition$additionalConditions"

        val query = entityManager.createQuery(queryString)
            .setParameter("oldPosition", oldPosition)
            .setParameter("newPosition", newPosition)

        setParameters(query, params)
        query.executeUpdate()
    }

    override fun incrementPositions(position: Int, params: NodeParams?) {
        val additionalConditions = buildAdditionalConditions(params)
        val queryString = "UPDATE NodeEntity e SET e.position = e.position + 1 WHERE e.position >= :position$additionalConditions"

        val query = entityManager.createQuery(queryString)
            .setParameter("position", position)

        setParameters(query, params)
        query.executeUpdate()
    }

    override fun decrementPositions(position: Int, params: NodeParams?) {
        val additionalConditions = buildAdditionalConditions(params)
        val queryString = "UPDATE NodeEntity e SET e.position = e.position - 1 WHERE e.position > :position$additionalConditions"

        val query = entityManager.createQuery(queryString)
            .setParameter("position", position)

        setParameters(query, params)
        query.executeUpdate()
    }

    override fun findMaxPosition(params: NodeParams?): Int? {
        val whereClause = buildWhereClause(params)
        val queryString = "SELECT MAX(e.position) FROM NodeEntity e$whereClause"

        val query = entityManager.createQuery(queryString, Int::class.javaObjectType)
        setParameters(query, params)

        return query.resultList.firstOrNull() as Int?
    }

    override fun findAllByOrderByPositionAsc(params: NodeParams?): List<NodeEntity> {
        val whereClause = buildWhereClause(params)
        val queryString = "SELECT e FROM NodeEntity e$whereClause ORDER BY e.position ASC"

        val query = entityManager.createQuery(queryString, NodeEntity::class.java)
        setParameters(query, params)

        return query.resultList
    }

    override fun findAllByOrderByPositionAsc(pageable: Pageable, params: NodeParams?): Page<NodeEntity> {
        val whereClause = buildWhereClause(params)

        // Count query
        val countQueryString = "SELECT COUNT(e) FROM NodeEntity e$whereClause"
        val countQuery = entityManager.createQuery(countQueryString, Long::class.javaObjectType)
        setParameters(countQuery, params)
        val total = countQuery.singleResult

        // If total is 0, return empty page
        if (total == 0L) {
            return PageImpl(emptyList(), pageable, 0)
        }

        // Data query with pagination
        val queryString = "SELECT e FROM NodeEntity e$whereClause ORDER BY e.position ASC"
        val query = entityManager.createQuery(queryString, NodeEntity::class.java)
        setParameters(query, params)

        // Apply pagination
        if (pageable.isPaged) {
            query.firstResult = pageable.offset.toInt()
            query.maxResults = pageable.pageSize
        }

        val content = query.resultList
        return PageImpl(content, pageable, total)
    }

    override fun deleteAllByParams(params: NodeParams?) {
        val whereClause = buildWhereClause(params)
        val queryString = "DELETE FROM NodeEntity e$whereClause"

        val query = entityManager.createQuery(queryString)
        setParameters(query, params)

        query.executeUpdate()
    }
}