package cz.binaryburst.treestructure.node.repository

import cz.binaryburst.generic.repository.OrderableRepository
import cz.binaryburst.treestructure.node.entity.NodeEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface NodeRepository : OrderableRepository<NodeEntity, Long> {

    @Query("SELECT n FROM NodeEntity n WHERE n.group.id = :groupId ORDER BY n.position ASC")
    fun findByGroupIdOrderByPosition(@Param("groupId") groupId: Long): List<NodeEntity>

    @Query("SELECT n FROM NodeEntity n WHERE n.parent.id = :parentId ORDER BY n.position ASC")
    fun findByParentIdOrderByPosition(@Param("parentId") parentId: Long): List<NodeEntity>

    @Query("SELECT n FROM NodeEntity n WHERE n.group.id = :groupId AND n.parent IS NULL ORDER BY n.position ASC")
    fun findRootNodesByGroupIdOrderByPosition(@Param("groupId") groupId: Long): List<NodeEntity>

    @Query("SELECT n FROM NodeEntity n WHERE n.group.id = :groupId")
    fun findByGroupId(@Param("groupId") groupId: Long): List<NodeEntity>

    @Query("SELECT n FROM NodeEntity n WHERE n.parent.id = :parentId")
    fun findByParentId(@Param("parentId") parentId: Long): List<NodeEntity>

    @Query("SELECT n FROM NodeEntity n WHERE n.group.id = :groupId AND n.parent IS NULL")
    fun findRootNodesByGroupId(@Param("groupId") groupId: Long): List<NodeEntity>

    @Query("SELECT n FROM NodeEntity n WHERE n.group.id = :groupId AND n.name = :name")
    fun findByGroupIdAndName(@Param("groupId") groupId: Long, @Param("name") name: String): List<NodeEntity>
}