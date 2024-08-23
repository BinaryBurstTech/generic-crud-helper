package cz.binaryburst.basetest.service.embeddable.repository

import cz.binaryburst.basetest.service.embeddable.entity.EmbeddedTestEntity
import cz.binaryburst.generic.repository.BaseRepository
import org.springframework.stereotype.Repository

@Repository
interface EmbeddedTestRepository : BaseRepository<EmbeddedTestEntity, Long>