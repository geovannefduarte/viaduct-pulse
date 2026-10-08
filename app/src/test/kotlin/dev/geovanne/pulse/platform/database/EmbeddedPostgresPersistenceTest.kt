package dev.geovanne.pulse.platform.database

import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.jdbc.core.simple.JdbcClient
import java.nio.file.Path

class EmbeddedPostgresPersistenceTest {
    @TempDir
    lateinit var dataDirectory: Path

    @Test
    fun `sync_state rows survive a restart on the same data directory`() {
        startEmbeddedPostgres(dataDirectory).use { postgres ->
            Flyway
                .configure()
                .dataSource(postgres.postgresDatabase)
                .load()
                .migrate()
            JdbcClient
                .create(postgres.postgresDatabase)
                .sql("insert into sync_state (source, watermark) values ('commits', '{\"sha\": \"abc123\"}')")
                .update()
        }

        startEmbeddedPostgres(dataDirectory).use { postgres ->
            val sha =
                JdbcClient
                    .create(postgres.postgresDatabase)
                    .sql("select watermark ->> 'sha' from sync_state where source = 'commits'")
                    .query(String::class.java)
                    .single()
            assertThat(sha).isEqualTo("abc123")
        }
    }
}
