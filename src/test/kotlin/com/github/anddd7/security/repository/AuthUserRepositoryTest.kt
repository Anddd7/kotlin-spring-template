package com.github.anddd7.security.repository

import com.github.anddd7.SQLScript
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@Sql(scripts = [SQLScript.AUTH_USER, SQLScript.USER_EXTENSION])
internal class AuthUserRepositoryTest {
    @Autowired
    private lateinit var authUserRepository: AuthUserRepository

    @Test
    fun `should return all users and related roles`() {
        val users = authUserRepository.findAll()
        assertThat(users).hasSize(4)

        assertThat(
            users.all {
                it.info.email.substringAfter("@").substringBefore(".").equals(it.employee.department, ignoreCase = true)
            }
        ).isTrue
    }
}
