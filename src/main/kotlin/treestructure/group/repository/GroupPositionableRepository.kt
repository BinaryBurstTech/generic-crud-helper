package cz.binaryburst.treestructure.group.repository

import cz.binaryburst.generic.repository.IOrderablePositionableRepository
import cz.binaryburst.treestructure.group.dto.GroupParams
import cz.binaryburst.treestructure.group.entity.GroupEntity
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class GroupPositionableRepository(
    entityManager: EntityManager
) : IOrderablePositionableRepository<GroupParams, GroupEntity>(
    entityManager = entityManager,
    GroupEntity::class.java
) {

    override fun incrementPositions(newPosition: Int, oldPosition: Int, params: GroupParams?) {
        val queryBuilder =
            StringBuilder("UPDATE GroupEntity e SET e.position = e.position + 1 WHERE e.position >= :newPosition AND e.position < :oldPosition")
        params?.zoneId?.let { queryBuilder.append(" AND e.zone.id = :zoneId") }

        val query = entityManager.createQuery(queryBuilder.toString())
            .setParameter("newPosition", newPosition)
            .setParameter("oldPosition", oldPosition)

        params?.zoneId?.let { query.setParameter("zoneId", it) }
        query.executeUpdate()
    }

    override fun decrementPositions(oldPosition: Int, newPosition: Int, params: GroupParams?) {
        val queryBuilder =
            StringBuilder("UPDATE GroupEntity e SET e.position = e.position - 1 WHERE e.position > :oldPosition AND e.position <= :newPosition")
        params?.zoneId?.let { queryBuilder.append(" AND e.zone.id = :zoneId") }

        val query = entityManager.createQuery(queryBuilder.toString())
            .setParameter("oldPosition", oldPosition)
            .setParameter("newPosition", newPosition)

        params?.zoneId?.let { query.setParameter("zoneId", it) }
        query.executeUpdate()
    }

    override fun incrementPositions(position: Int, params: GroupParams?) {
        val queryBuilder =
            StringBuilder("UPDATE GroupEntity e SET e.position = e.position + 1 WHERE e.position >= :position")
        params?.zoneId?.let { queryBuilder.append(" AND e.zone.id = :zoneId") }

        val query = entityManager.createQuery(queryBuilder.toString())
            .setParameter("position", position)

        params?.zoneId?.let { query.setParameter("zoneId", it) }
        query.executeUpdate()
    }

    override fun decrementPositions(position: Int, params: GroupParams?) {
        val queryBuilder =
            StringBuilder("UPDATE GroupEntity e SET e.position = e.position - 1 WHERE e.position > :position")
        params?.zoneId?.let { queryBuilder.append(" AND e.zone.id = :zoneId") }

        val query = entityManager.createQuery(queryBuilder.toString())
            .setParameter("position", position)

        params?.zoneId?.let { query.setParameter("zoneId", it) }
        query.executeUpdate()
    }

    override fun findMaxPosition(params: GroupParams?): Int? {
        val queryBuilder = StringBuilder("SELECT MAX(e.position) FROM GroupEntity e")
        params?.zoneId?.let { queryBuilder.append(" WHERE e.zone.id = :zoneId") }

        val query = entityManager.createQuery(queryBuilder.toString(), Int::class.javaObjectType)
        params?.zoneId?.let { query.setParameter("zoneId", it) }

        return query.resultList.firstOrNull() as Int?
    }

    override fun findAllByOrderByPositionAsc(params: GroupParams?): List<GroupEntity> {
        val queryBuilder = StringBuilder("SELECT e FROM GroupEntity e")
        params?.zoneId?.let { queryBuilder.append(" WHERE e.zone.id = :zoneId") }
        queryBuilder.append(" ORDER BY e.position ASC")

        val query = entityManager.createQuery(queryBuilder.toString(), GroupEntity::class.java)
        params?.zoneId?.let { query.setParameter("zoneId", it) }

        return query.resultList
    }

    override fun findAllByOrderByPositionAsc(pageable: Pageable, params: GroupParams?): Page<GroupEntity> {
        // Count query
        val countQueryBuilder = StringBuilder("SELECT COUNT(e) FROM GroupEntity e")
        params?.zoneId?.let { countQueryBuilder.append(" WHERE e.zone.id = :zoneId") }

        val countQuery = entityManager.createQuery(countQueryBuilder.toString(), Long::class.javaObjectType)
        params?.zoneId?.let { countQuery.setParameter("zoneId", it) }
        val total = countQuery.singleResult

        // If total is 0, return empty page
        if (total == 0L) {
            return PageImpl(emptyList(), pageable, 0)
        }

        // Data query with pagination
        val queryBuilder = StringBuilder("SELECT e FROM GroupEntity e")
        params?.zoneId?.let { queryBuilder.append(" WHERE e.zone.id = :zoneId") }
        queryBuilder.append(" ORDER BY e.position ASC")

        val query = entityManager.createQuery(queryBuilder.toString(), GroupEntity::class.java)
        params?.zoneId?.let { query.setParameter("zoneId", it) }

        // Apply pagination
        if (pageable.isPaged) {
            query.firstResult = pageable.offset.toInt()
            query.maxResults = pageable.pageSize
        }

        val content = query.resultList
        return PageImpl(content, pageable, total)
    }

    override fun deleteAllByParams(params: GroupParams?) {
        val queryBuilder = StringBuilder("DELETE FROM GroupEntity e")
        params?.zoneId?.let { queryBuilder.append(" WHERE e.zone.id = :zoneId") }

        val query = entityManager.createQuery(queryBuilder.toString())
        params?.zoneId?.let { query.setParameter("zoneId", it) }

        query.executeUpdate()
    }
}