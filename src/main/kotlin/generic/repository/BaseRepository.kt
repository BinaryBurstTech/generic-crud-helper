package cz.binaryburst.generic.repository

import cz.binaryburst.generic.entity.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

/**
 * Abstract base repository interface for managing entities.
 *
 * @param T The type of the entity managed by the repository.
 * @param ID The type of the entity identifier.
 */
@NoRepositoryBean
interface BaseRepository<T : BaseEntity<ID>, ID : Serializable> : JpaRepository<T, ID>
