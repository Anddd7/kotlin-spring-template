package com.github.anddd7.core.transactional

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.concurrent.Executors
import javax.persistence.*


@Component
class ExceptionTrigger {
    fun isTriggeredAt1(part1: Part1) {}
    fun isTriggeredAt2(part2: Part2) {}
    fun isTriggeredAt3(part3: Part3) {}
    fun isTriggeredAt4(hub: HubPO) {}
    fun isCoroutine() = false
}

@Service
class HubService(
    private val repository: HubRepository,
    private val part1Dao: Part1Dao,
    private val part2Dao: Part2Dao,
    private val part3Dao: Part3Dao,
    private val et: ExceptionTrigger
) {
    private val executorService = Executors.newFixedThreadPool(2)

    private fun coCreate(name1: String, name2: String, name3: String): HubModel {
        val printTransactionalInfo: (String) -> Unit = {
            val threadName = Thread.currentThread().name
            val transactionName = TransactionSynchronizationManager.getCurrentTransactionName()
            println("$it, Thread: $threadName, Transaction: $transactionName")
        }

        /* Verify the transaction in multiple thread */
        printTransactionalInfo("initial")
        Thread { printTransactionalInfo("thread") }.start()
        executorService.submit { printTransactionalInfo("thread pool") }

        return runBlocking(Dispatchers.IO) {
            /* Wait for thread done */
            delay(100)

            /* Verify the transaction in coroutines */
            val deferredPart1 = async {
                printTransactionalInfo("part1")
                part1Dao.save(Part1(name = name1)).also(et::isTriggeredAt1)
            }
            val deferredPart2 = async {
                printTransactionalInfo("part1")
                part2Dao.save(Part2(name = name2)).also(et::isTriggeredAt2)
            }
            val deferredPart3 = async {
                printTransactionalInfo("part1")
                part3Dao.save(Part3(name = name3)).also(et::isTriggeredAt3)
            }

            val part1 = deferredPart1.await()
            val part2 = deferredPart2.await()
            val part3 = deferredPart3.await()

            val entity = repository.save(
                HubPO(
                    id = 0,
                    part1 = part1.id,
                    part2 = part2.id,
                    part3 = part3.id
                )
            )
                .also(et::isTriggeredAt4)

            HubModel(entity.id, part1, part2, part3)
        }
    }

    private fun seqCreate(name1: String, name2: String, name3: String): HubModel {
        val part1 = part1Dao.save(Part1(name = name1)).also(et::isTriggeredAt1)
        val part2 = part2Dao.save(Part2(name = name2)).also(et::isTriggeredAt2)
        val part3 = part3Dao.save(Part3(name = name3)).also(et::isTriggeredAt3)

        val entity = repository.save(HubPO(id = 0, part1 = part1.id, part2 = part2.id, part3 = part3.id))
            .also(et::isTriggeredAt4)
        return HubModel(entity.id, part1, part2, part3)
    }

    private fun coUpdate(hub: HubModel): HubModel {
        return runBlocking {
            val deferredPart1 = async { part1Dao.save(hub.part1).also(et::isTriggeredAt1) }
            val deferredPart2 = async { part2Dao.save(hub.part2).also(et::isTriggeredAt2) }
            val deferredPart3 = async { part3Dao.save(hub.part3).also(et::isTriggeredAt3) }


            val part1 = deferredPart1.await()
            val part2 = deferredPart2.await()
            val part3 = deferredPart3.await()

            HubModel(hub.id, part1, part2, part3)
        }
    }

    private fun seqUpdate(hub: HubModel): HubModel {
        val part1 = part1Dao.save(hub.part1).also(et::isTriggeredAt1)
        val part2 = part2Dao.save(hub.part2).also(et::isTriggeredAt2)
        val part3 = part3Dao.save(hub.part3).also(et::isTriggeredAt3)

        return HubModel(hub.id, part1, part2, part3)
    }

    @Transactional
    fun create(name1: String, name2: String, name3: String): HubModel {
        return if (et.isCoroutine()) coCreate(name1, name2, name3)
        else seqCreate(name1, name2, name3)
    }

    @Transactional
    fun update(hub: HubModel): HubModel {
        return if (et.isCoroutine()) coUpdate(hub)
        else seqUpdate(hub)
    }

    fun find(id: Long): HubModel {
        val entity = repository.getOne(id)

        return runBlocking(Dispatchers.IO) {
            val deferredPart1 = async { part1Dao.find(entity.part1) }
            val deferredPart2 = async { part2Dao.find(entity.part2) }
            val deferredPart3 = async { part3Dao.find(entity.part3) }

            val part1 = deferredPart1.await()
            val part2 = deferredPart2.await()
            val part3 = deferredPart3.await()

            HubModel(id, part1, part2, part3)
        }
    }

    fun findAll(): List<HubModel> {
        val hubs = repository.findAll()

        return runBlocking(Dispatchers.IO) {
            hubs
                .map {
                    val part1 = async { part1Dao.find(it.part1) }
                    val part2 = async { part2Dao.find(it.part2) }
                    val part3 = async { part3Dao.find(it.part3) }
                    it to Triple(part1, part2, part3)
                }
                .map {
                    val (id, deferred) = it

                    val part1 = deferred.first.await()
                    val part2 = deferred.second.await()
                    val part3 = deferred.third.await()

                    HubModel(id.id, part1, part2, part3)
                }
        }
    }
}

/* model + dao */
@Component
class Part1Dao(private val repository: Part1Repository) {
    fun save(model: Part1) = repository.save(model.run { Part1PO(id, name) }).run { Part1(id, name) }
    fun find(id: Long) = repository.getOne(id).run { Part1(id, name) }
}

@Component
class Part2Dao(private val repository: Part2Repository) {
    fun save(model: Part2) = repository.save(model.run { Part2PO(id, name) }).run { Part2(id, name) }
    fun find(id: Long) = repository.getOne(id).run { Part2(id, name) }
}

@Component
class Part3Dao(private val repository: Part3Repository) {
    fun save(model: Part3) = repository.save(model.run { Part3PO(id, name) }).run { Part3(id, name) }
    fun find(id: Long) = repository.getOne(id).run { Part3(id, name) }
}


data class HubModel(
    val id: Long = 0L,
    val part1: Part1,
    val part2: Part2,
    val part3: Part3,
)

data class Part1(
    val id: Long = 0L,
    val name: String
)

data class Part2(
    val id: Long = 0L,
    val name: String
)

data class Part3(
    val id: Long = 0L,
    val name: String
)

/* repository */
@Repository
interface HubRepository : JpaRepository<HubPO, Long>

@Entity
@Table(name = "transaction_hub")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
data class HubPO(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name = "id1")
    val part1: Long,
    @Column(name = "id2")
    val part2: Long,
    @Column(name = "id3")
    val part3: Long,
)


@Repository
interface Part1Repository : JpaRepository<Part1PO, Long>

@Entity
@Table(name = "transaction_part1")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
data class Part1PO(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name = "name")
    val name: String
)

@Repository
interface Part2Repository : JpaRepository<Part2PO, Long>

@Entity
@Table(name = "transaction_part2")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
data class Part2PO(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name = "name")
    val name: String
)

@Repository
interface Part3Repository : JpaRepository<Part3PO, Long>

@Entity
@Table(name = "transaction_part3")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
data class Part3PO(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name = "name")
    val name: String
)