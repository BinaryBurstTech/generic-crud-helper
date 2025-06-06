package cz.binaryburst.treestructure.zone.repository

import cz.binaryburst.generic.repository.BaseRepository
import cz.binaryburst.treestructure.zone.entity.ZoneEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ZoneRepository : BaseRepository<ZoneEntity, Long> {

    @Query("SELECT z FROM ZoneEntity z WHERE z.root.id = :rootId")
    fun findByRootId(@Param("rootId") rootId: Long): List<ZoneEntity>
}