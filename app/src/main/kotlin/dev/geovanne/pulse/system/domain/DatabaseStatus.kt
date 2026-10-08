package dev.geovanne.pulse.system.domain

data class DatabaseStatus(
    val postgresVersion: String,
    val appliedMigrations: List<AppliedMigration>,
)

data class AppliedMigration(
    val version: String,
    val description: String,
)
