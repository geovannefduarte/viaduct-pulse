package dev.geovanne.pulse.platform.database

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.jdbc.core.simple.JdbcClient
import java.nio.file.Path

class EmbeddedPostgresDurabilityTest {
    @TempDir
    lateinit var dataDirectory: Path

    @Test
    fun `commits are flushed to disk`() {
        startEmbeddedPostgres(dataDirectory).use { postgres ->
            val jdbcClient = JdbcClient.create(postgres.postgresDatabase)

            fun setting(name: String) = jdbcClient.sql("show $name").query(String::class.java).single()

            assertThat(setting("fsync")).isEqualTo("on")
            assertThat(setting("synchronous_commit")).isEqualTo("on")
        }
    }
}
