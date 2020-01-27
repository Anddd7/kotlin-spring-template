package com.github.anddd7.core.api

import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER)
class AuthorizationApiTest {
  @Autowired
  private lateinit var mvc: MockMvc

  @Test
  fun `should not access dashboard when user have not login`() {
    mvc.perform(get("/api/dashboard")).andExpect(status().isForbidden)
  }

  @Test
  @WithMockUser(authorities = ["DASHBOARD"])
  fun `should access dashboard when user login with permission`() {
    mvc.perform(get("/api/dashboard")).andExpect(status().isOk)
  }

  @Test
  @WithMockUser(authorities = ["DASHBOARD"])
  fun `should not access order when user dont have permission`() {
    mvc.perform(get("/api/order")).andExpect(status().isForbidden)
  }

  @Test
  @WithMockUser(authorities = ["Order"])
  fun `should access order when user login with permission`() {
    mvc.perform(get("/api/order")).andExpect(status().isForbidden)
  }
}
