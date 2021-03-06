package com.github.anddd7.core.transactional

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfigureEmbeddedDatabase
//@Transactional
class TransactionalTest {

    @Autowired
    private lateinit var hubService: HubService

    @MockkBean
    private lateinit var et: ExceptionTrigger

    @Autowired
    private lateinit var hubRepository: HubRepository

    @Autowired
    private lateinit var part1Repository: Part1Repository

    @Autowired
    private lateinit var part2Repository: Part2Repository

    @Autowired
    private lateinit var part3Repository: Part3Repository

    @BeforeEach
    internal fun setUp() {
        // test coroutine
        every { et.isCoroutine() } returns true
        // test sequence
        every { et.isCoroutine() } returns false
    }

    @AfterEach
    internal fun tearDown() {
        hubRepository.deleteAll()
        part1Repository.deleteAll()
        part2Repository.deleteAll()
        part3Repository.deleteAll()
    }

    private fun noEx() {
        every { et.isTriggeredAt1(any()) } just Runs
        every { et.isTriggeredAt2(any()) } just Runs
        every { et.isTriggeredAt3(any()) } just Runs
        every { et.isTriggeredAt4(any()) } just Runs
    }

    @Test
    fun `create successful`() {
        noEx()
        val hub1 = hubService.create("test-1-1", "test-1-2", "test-1-3")
        val hub2 = hubService.create("test-2-1", "test2-2-", "test-2-3")

        val models = hubService.findAll()

        assertThat(models).isEqualTo(listOf(hub1, hub2))
        assertThat(hubRepository.count()).isEqualTo(2)
        assertThat(part1Repository.count()).isEqualTo(2)
        assertThat(part2Repository.count()).isEqualTo(2)
        assertThat(part3Repository.count()).isEqualTo(2)
    }

    @Test
    fun `update successful`() {
        noEx()
        val hub1 = hubService.create("test-1-1", "test-1-2", "test-1-3")
        val hub2 = hubService.create("test-2-1", "test2-2-", "test-2-3")

        val updatedHub1 = hubService.update(
            hub1.run {
                copy(
                    part1 = part1.run { copy(name = "updated-$name") },
                    part2 = part2.run { copy(name = "updated-$name") },
                    part3 = part3.run { copy(name = "updated-$name") },
                )
            })
        val updatedHub2 = hubService.update(
            hub2.run {
                copy(
                    part1 = part1.run { copy(name = "updated-$name") },
                    part2 = part2.run { copy(name = "updated-$name") },
                    part3 = part3.run { copy(name = "updated-$name") },
                )
            }
        )

        val models = hubService.findAll()

        assertThat(models).isEqualTo(listOf(updatedHub1, updatedHub2))
        assertThat(hubRepository.count()).isEqualTo(2)
        assertThat(part1Repository.count()).isEqualTo(2)
        assertThat(part2Repository.count()).isEqualTo(2)
        assertThat(part3Repository.count()).isEqualTo(2)
    }

    @Test
    fun `create failed after saved part1`() {
        noEx()
        val success = hubService.create("test-1-1", "test-1-2", "test-1-3")

        every { et.isTriggeredAt1(any()) } answers {
            throw IllegalStateException("date:" + firstArg())
        }
        assertThrows<IllegalStateException> {
            hubService.create("test-2-1", "test2-2-", "test-2-3")
        }

        val models = hubService.findAll()

        assertThat(models).isEqualTo(listOf(success))
        assertThat(hubRepository.count()).isEqualTo(1)
        assertThat(part1Repository.count()).isEqualTo(1)
        assertThat(part2Repository.count()).isEqualTo(1)
        assertThat(part3Repository.count()).isEqualTo(1)
    }

    @Test
    fun `create failed after saved part2`() {
        noEx()
        val success = hubService.create("test-1-1", "test-1-2", "test-1-3")

        every { et.isTriggeredAt2(any()) } answers {
            throw IllegalStateException("date:" + firstArg())
        }
        assertThrows<IllegalStateException> {
            hubService.create("test-2-1", "test2-2-", "test-2-3")
        }

        val models = hubService.findAll()

        assertThat(models).isEqualTo(listOf(success))
        assertThat(hubRepository.count()).isEqualTo(1)
        assertThat(part1Repository.count()).isEqualTo(1)
        assertThat(part2Repository.count()).isEqualTo(1)
        assertThat(part3Repository.count()).isEqualTo(1)
    }

    @Test
    fun `create failed after saved part3`() {
        noEx()
        val success = hubService.create("test-1-1", "test-1-2", "test-1-3")

        every { et.isTriggeredAt3(any()) } answers {
            throw IllegalStateException("date:" + firstArg())
        }
        assertThrows<IllegalStateException> {
            hubService.create("test-2-1", "test2-2-", "test-2-3")
        }

        val models = hubService.findAll()

        assertThat(models).isEqualTo(listOf(success))
        assertThat(hubRepository.count()).isEqualTo(1)
        assertThat(part1Repository.count()).isEqualTo(1)
        assertThat(part2Repository.count()).isEqualTo(1)
        assertThat(part3Repository.count()).isEqualTo(1)
    }

    @Test
    fun `create failed after saved hub`() {
        noEx()
        val success = hubService.create("test-1-1", "test-1-2", "test-1-3")

        every { et.isTriggeredAt4(any()) } answers {
            throw IllegalStateException("date:" + firstArg())
        }
        assertThrows<IllegalStateException> {
            hubService.create("test-2-1", "test2-2-", "test-2-3")
        }

        val models = hubService.findAll()

        assertThat(models).isEqualTo(listOf(success))
        assertThat(hubRepository.count()).isEqualTo(1)
        assertThat(part1Repository.count()).isEqualTo(1)
        assertThat(part2Repository.count()).isEqualTo(1)
        assertThat(part3Repository.count()).isEqualTo(1)
    }

    @Test
    fun `update failed after saved part1`() {
        noEx()
        val hub = hubService.create("test-1-1", "test-1-2", "test-1-3")
        val expectedUpdate = hub.run {
            copy(
                part1 = part1.run { copy(name = "updated-$name") },
                part2 = part2.run { copy(name = "updated-$name") },
                part3 = part3.run { copy(name = "updated-$name") },
            )
        }

        every { et.isTriggeredAt1(any()) } answers {
            throw IllegalStateException("date:" + firstArg())
        }
        assertThrows<IllegalStateException> {
            hubService.update(expectedUpdate)
        }

        val models = hubService.findAll()

        assertThat(models).isEqualTo(listOf(hub))
        assertThat(part1Repository.getOne(hub.part1.id).name).doesNotStartWith("updated")
        assertThat(part2Repository.getOne(hub.part2.id).name).doesNotStartWith("updated")
        assertThat(part3Repository.getOne(hub.part3.id).name).doesNotStartWith("updated")
    }

    @Test
    fun `update failed after saved part2`() {
        noEx()
        val hub = hubService.create("test-1-1", "test-1-2", "test-1-3")
        val expectedUpdate = hub.run {
            copy(
                part1 = part1.run { copy(name = "updated-$name") },
                part2 = part2.run { copy(name = "updated-$name") },
                part3 = part3.run { copy(name = "updated-$name") },
            )
        }

        every { et.isTriggeredAt2(any()) } answers {
            throw IllegalStateException("date:" + firstArg())
        }
        assertThrows<IllegalStateException> {
            hubService.update(expectedUpdate)
        }

        val models = hubService.findAll()

        assertThat(models).isEqualTo(listOf(hub))
        assertThat(part1Repository.getOne(hub.part1.id).name).doesNotStartWith("updated")
        assertThat(part2Repository.getOne(hub.part2.id).name).doesNotStartWith("updated")
        assertThat(part3Repository.getOne(hub.part3.id).name).doesNotStartWith("updated")
    }

    @Test
    fun `update failed after saved part3`() {
        noEx()
        val hub = hubService.create("test-1-1", "test-1-2", "test-1-3")
        val expectedUpdate = hub.run {
            copy(
                part1 = part1.run { copy(name = "updated-$name") },
                part2 = part2.run { copy(name = "updated-$name") },
                part3 = part3.run { copy(name = "updated-$name") },
            )
        }

        every { et.isTriggeredAt3(any()) } answers {
            throw IllegalStateException("date:" + firstArg())
        }
        assertThrows<IllegalStateException> {
            hubService.update(expectedUpdate)
        }

        val models = hubService.findAll()

        assertThat(models).isEqualTo(listOf(hub))
        assertThat(part1Repository.getOne(hub.part1.id).name).doesNotStartWith("updated")
        assertThat(part2Repository.getOne(hub.part2.id).name).doesNotStartWith("updated")
        assertThat(part3Repository.getOne(hub.part3.id).name).doesNotStartWith("updated")
    }
}