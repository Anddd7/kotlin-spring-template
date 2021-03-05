package com.github.anddd7.core.transactional

import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureEmbeddedDatabase
@Transactional
class TransactionalTest {

    @Autowired
    private lateinit var hubService: HubService

    @Test
    fun `create successful`() {
        val hub1 = hubService.create("test-1-1", "test-1-2", "test-1-3")
        val hub2 = hubService.create("test-2-1", "test2-2-", "test-2-3")

        val models = hubService.findAll()

        Assertions.assertThat(listOf(hub1, hub2)).isEqualTo(models)
    }

    @Test
    fun `update successful`() {
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

        Assertions.assertThat(listOf(updatedHub1, updatedHub2)).isEqualTo(models)
    }
}