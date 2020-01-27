package com.github.anddd7.core.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER)
internal class TempApiTest {
  @Autowired
  private lateinit var mvc: MockMvc
  private val objectMapper = ObjectMapper()

  @Test
  fun `should return version`() {
    mvc.perform(get("/temp/version"))
        .andExpect(content().string("v0.0.1"))
  }

  @Test
  fun `should return healthy info`() {
    mvc.perform(get("/temp/ping"))
        .andExpect(jsonPath("$.version").value("v0.0.1"))
        .andExpect(jsonPath("$.active").value("ok"))
  }

  @Test
  fun `should pass validation of request parameters and body`() {
    val requestBody = mapOf(
        "userInfo" to mapOf(
            "name" to "and777",
            "age" to 10,
            "email" to "liaoad_space@sina.com",
            "phone" to mapOf(
                "areaCode" to "86",
                "number" to "12345678901"
            )
        ),
        "moreDescription" to mapOf(
            "title" to "title",
            "content" to "more description should more than 10"
        ),
        "ranges" to listOf(
            mapOf(
                "first" to 1,
                "second" to 2
            )
        )
    )

    mvc.perform(
        post("/temp/validate")
            .param("correlationId", "AB12976551827EH1")
            .param("operations", "validate", "save", "test")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody))
    ).andExpect(status().isOk)
  }
}
