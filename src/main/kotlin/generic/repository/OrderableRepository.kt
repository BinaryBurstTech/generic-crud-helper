package cz.binaryburst.generic.repository

import cz.binaryburst.generic.entity.OrderableEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.query.Param
import java.io.Serializable

@NoRepositoryBean
interface OrderableRepository<T : OrderableEntity<ID>, ID : Serializable> : JpaRepository<T, ID>,
    BaseRepository<T, ID> {

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.position = e.position + 1 WHERE e.position >= :newPosition AND e.position < :oldPosition")
    fun incrementPositions(
        @Param("newPosition") newPosition: Int,
        @Param("oldPosition") oldPosition: Int,
    )

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.position = e.position - 1 WHERE e.position > :oldPosition AND e.position <= :newPosition")
    fun decrementPositions(
        @Param("oldPosition") oldPosition: Int,
        @Param("newPosition") newPosition: Int,
    )

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.position = e.position + 1 WHERE e.position >= :position")
    fun incrementPositions(@Param("position") position: Int)

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.position = e.position - 1 WHERE e.position > :position")
    fun decrementPositions(@Param("position") position: Int)


    @Query("SELECT MAX(e.position) FROM #{#entityName} e")
    fun findMaxPosition(): Int?

    fun findAllByOrderByPositionAsc(): List<T>

}