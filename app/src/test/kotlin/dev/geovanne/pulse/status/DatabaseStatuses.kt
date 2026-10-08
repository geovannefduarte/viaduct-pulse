package dev.geovanne.pulse.status

object DatabaseStatuses {
    fun aDatabaseStatus(): DatabaseStatus =
        DatabaseStatus(
            postgresVersion = "18.6",
            appliedMigrations = listOf(AppliedMigration("1", "create sync state")),
        )
}
