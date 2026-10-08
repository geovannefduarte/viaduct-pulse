package dev.geovanne.pulse

import dev.geovanne.pulse.system.application.DatabaseStatusService
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.nio.file.Files

@SpringBootTest
@AutoConfigureMockMvc
class PulseApplicationTest {
    @Autowired
    lateinit var databaseStatusService: DatabaseStatusService

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `starts on embedded Postgres 18 with the V1 migration applied`() {
        val status = databaseStatusService.current()

        assertThat(status.postgresVersion).startsWith("18.")
        assertThat(status.appliedMigrations.map { it.version }).containsExactly("1")
    }

    @Test
    fun `serves the status card over htmx`() {
        mockMvc
            .perform(get("/status").header("HX-Request", "true").header("HX-Target", "status-card"))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("id=\"postgres-version\">18.")))
    }

    @Test
    fun `serves the htmx webjar without a version in the URL`() {
        mockMvc
            .perform(get("/webjars/htmx.org/dist/htmx.min.js"))
            .andExpect(status().isOk)
    }

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun postgresDataDirectory(registry: DynamicPropertyRegistry) {
            val dataDirectory = Files.createTempDirectory("pulse-pgdata")
            registry.add("pulse.postgres.data-directory") { dataDirectory.toString() }
        }
    }
}
