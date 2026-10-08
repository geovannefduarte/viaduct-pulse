package dev.geovanne.pulse.system.persistence

import dev.geovanne.pulse.system.domain.AppliedMigration
import dev.geovanne.pulse.system.domain.DatabaseStatus
import dev.geovanne.pulse.system.domain.DatabaseStatusSource
import org.flywaydb.core.Flyway
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Component

@Component
class PostgresDatabaseStatusSource(
    private val jdbcClient: JdbcClient,
    private val flyway: Flyway,
) : DatabaseStatusSource {
    override fun current(): DatabaseStatus =
        DatabaseStatus(
            postgresVersion = jdbcClient.sql("show server_version").query(String::class.java).single(),
            appliedMigrations =
                flyway.info().applied().map { AppliedMigration(it.version.version, it.description) },
        )
}
