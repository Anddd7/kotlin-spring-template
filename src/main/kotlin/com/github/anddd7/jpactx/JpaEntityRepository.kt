package com.github.anddd7.jpactx

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.hibernate.SessionFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@RestController
@RequestMapping("/jpa-entity")
class JpaEntityController(
    private val repository: JpaEntityRepository,
    private val sessionFactory: SessionFactory,
) {
    @GetMapping("/clearup")
    fun clearup() {
        repository.deleteAllInBatch()
    }

    @GetMapping("/repo-save")
    fun repoSave() {
        val entity = JpaEntity(name = "repo-save").apply { description = "just created" }
        repository.save(entity)
        entity.inspect()

        entity.description = "updated after save"
        entity.inspect()

        repository.getById(entity.id).inspect()
    }

    @GetMapping("/persist")
    fun persist() {
        println("current size: ${repository.count()}")

        val entity = JpaEntity(name = "persist").apply { description = "just created" }
        val session = sessionFactory.openSession()
        session.persist(entity)
        entity.inspect()

        entity.description = "updated after persist"
        entity.inspect()

        kotlin.runCatching {
            repository.getById(entity.id).inspect() // no save
        }.onFailure {
            println("result size: ${repository.count()}")
            println("persist wont update the entity id")
        }
    }

    @GetMapping("/save")
    fun save() {
        val entity = JpaEntity(name = "save").apply { description = "just created" }
        val session = sessionFactory.openSession()
        session.save(entity)
        entity.inspect()

        entity.description = "updated after save" // not work
        entity.inspect()

        repository.getById(entity.id).inspect()
    }

    @GetMapping("/detach")
    fun detach() {
        val entity = JpaEntity(name = "detach").apply { description = "just created" }
        val session = sessionFactory.openSession()
        session.save(entity)
        entity.inspect()

        session.evict(entity)
        entity.description = "updated after evict" // not work
        entity.inspect()

        repository.getById(entity.id).inspect()
    }

    @GetMapping("/merge")
    fun merge() {
        val entity = JpaEntity(name = "merge").apply { description = "just created" }
        val session = sessionFactory.openSession()
        session.save(entity)
        entity.inspect()

        session.evict(entity)
        entity.description = "updated after evict"
        entity.inspect()

        session.merge(entity)
        repository.getById(entity.id).inspect()
    }

    @GetMapping("/update")
    fun update() {
        val entity = JpaEntity(name = "update").apply { description = "just created" }
        val session = sessionFactory.openSession()
        // kotlin.runCatching { session.update(entity) }.onFailure {
        //     println("update failed for new entity")
        // }

        session.save(entity)
        entity.inspect()

        // session.evict(entity) // will lost control
        entity.description = "updated after evict"
        entity.inspect()

        session.update(entity)
        repository.getById(entity.id).inspect()
    }

    private fun JpaEntity.inspect() {
        println(
            """
                | saved entity:
                id: $id, name: $name, description: $description
            """.trimIndent()
        )
    }
}

@Repository
interface JpaEntityRepository : JpaRepository<JpaEntity, Long>

@Entity
@Table(name = "jpa_entities")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
class JpaEntity(
    @Column(name = "name")
    val name: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "description")
    var description: String? = null

    fun isNew() = id == 0L
}
