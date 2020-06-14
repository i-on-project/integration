package org.ionproject.integration

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(properties = ["ion.core-base-url = test", "ion.core-token = test", "ion.core-request-timeout-seconds = 1"])
class IOnIntegrationApplicationTests {

    @Test
    fun contextLoads() {
    }
}
