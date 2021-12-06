package com.github.anddd7.bitwise

import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase
class BitwiseOperatorTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `should search column by bitwise`() {
        mvc.get("/bitwise?available=4")
            .andExpect {
                status { isOk() }
                content { string("3,5,6,7") }
            }
    }
}
