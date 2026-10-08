package dev.geovanne.pulse.status

import org.flywaydb.core.Flyway
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Service

@Service
class DatabaseStatusService(
    private val jdbcClient: JdbcClient,
    private val flyway: Flyway,
) {
    fun current(): DatabaseStatus =
        DatabaseStatus(
            postgresVersion = jdbcClient.sql("show server_version").query(String::class.java).single(),
            appliedMigrations =
                flyway.info().applied().map { AppliedMigration(it.version.version, it.description) },
        )
}
