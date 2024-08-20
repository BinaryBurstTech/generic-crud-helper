package cz.binaryburst.basetest.service.repository

import cz.binaryburst.generic.repository.BaseRepository
import org.springframework.stereotype.Repository
import cz.binaryburst.basetest.service.entity.BaseTestEntity

@Repository
interface BaseTestRepository : BaseRepository<BaseTestEntity, Long>