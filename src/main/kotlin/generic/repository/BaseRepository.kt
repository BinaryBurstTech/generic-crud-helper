package cz.binaryburst.generic.repository

import cz.binaryburst.generic.entity.BaseEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

/**
 * Abstract base repository interface for managing entities.
 *
 * @param T The type of the entity managed by the repository.
 * @param ID The type of the entity identifier.
 */
@NoRepositoryBean
interface BaseRepository<T : BaseEntity<ID>, ID : Serializable> : JpaRepository<T, ID> {
    /**
     * Finds all entities with pagination support.
     *
     * @param pageable Pagination information.
     * @return A page of entities.
     */
    fun findAllBy(pageable: Pageable): Page<T>

    /**
     * Checks if an entity with the given ID exists.
     *
     * @param id The ID to check.
     * @return True if an entity with the given ID exists, false otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.id = :id")
    fun existsWithId(id: ID): Boolean
}