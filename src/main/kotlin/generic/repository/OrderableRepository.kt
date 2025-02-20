package cz.binaryburst.generic.repository

import cz.binaryburst.generic.entity.OrderableEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

@NoRepositoryBean
interface OrderableRepository<T : OrderableEntity<ID>, ID : Serializable> : JpaRepository<T, ID>, BaseRepository<T, ID>