package cz.binaryburst.basetest.service.repository

import cz.binaryburst.basetest.service.entity.BaseTestEntity
import cz.binaryburst.generic.repository.BaseRepository
import org.springframework.stereotype.Repository

@Repository
interface BaseTestRepository : BaseRepository<BaseTestEntity, Long>