package cz.binaryburst.treestructure.group.repository

import cz.binaryburst.generic.repository.OrderableRepository
import cz.binaryburst.treestructure.group.entity.GroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository : OrderableRepository<GroupEntity, Long> {

    @Query("SELECT g FROM GroupEntity g WHERE g.zone.id = :zoneId ORDER BY g.position ASC")
    fun findByZoneIdOrderByPosition(@Param("zoneId") zoneId: Long): List<GroupEntity>

    @Query("SELECT g FROM GroupEntity g WHERE g.zone.id = :zoneId")
    fun findByZoneId(@Param("zoneId") zoneId: Long): List<GroupEntity>
}