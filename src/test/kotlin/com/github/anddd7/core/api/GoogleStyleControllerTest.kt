package com.github.anddd7.core.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.net.URLEncoder
import java.nio.charset.Charset

@ActiveProfiles("test")
@WebMvcTest(GoogleStyleController::class, excludeAutoConfiguration = [SecurityAutoConfiguration::class])
internal class GoogleStyleControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc
    private val id = "1a!@#\$%^&*():print"
    private val encoded = URLEncoder.encode(id, Charset.defaultCharset())

    @Test
    fun `should get things`() {
        mvc.get("/google/${encoded}").andExpect { content { string(id) } }
    }

    @Test
    fun `should print things`() {
        mvc.get("/google/${encoded}:print").andExpect { content { string("@<${id}>@") } }
    }
}
