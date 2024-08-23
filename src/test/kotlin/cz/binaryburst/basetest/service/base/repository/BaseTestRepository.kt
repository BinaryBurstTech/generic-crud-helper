package cz.binaryburst.basetest.service.base.repository

import cz.binaryburst.basetest.service.base.entity.BaseTestEntity
import cz.binaryburst.generic.repository.BaseRepository
import org.springframework.stereotype.Repository

@Repository
interface BaseTestRepository : BaseRepository<BaseTestEntity, Long>