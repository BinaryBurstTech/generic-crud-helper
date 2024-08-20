package cz.binaryburst.generic.repository

import cz.binaryburst.generic.entity.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

@NoRepositoryBean
interface BaseRepository<T : BaseEntity<ID>, ID : Serializable> : JpaRepository<T, ID>

