package cz.binaryburst.treestructure.root.repository

import cz.binaryburst.generic.repository.BaseRepository
import cz.binaryburst.treestructure.root.entity.RootEntity
import org.springframework.stereotype.Repository

@Repository
interface RootRepository : BaseRepository<RootEntity, Long>